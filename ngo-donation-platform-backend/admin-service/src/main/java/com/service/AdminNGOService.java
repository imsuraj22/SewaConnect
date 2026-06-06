package com.service;

import com.dto.NGODocumentDto;
import com.dto.NGODto;
import com.entity.NGOData;
import com.entity.NGODocumentData;
import com.repository.NGODataRepo;
import com.repository.NGODocumentDataRepo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AdminNGOService {
    private final KafkaTemplate<String, NGODto> ngoKafkaTemplate;
    private final KafkaTemplate<String, Long> longKafkaTemplate;
    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    private final NGODataRepo ngoDataRepo;
    private final NGODocumentDataRepo ngoDocumentDataRepo;

    public AdminNGOService(RestTemplate restTemplate,KafkaTemplate<String, Long> longKafkaTemplate,
                           KafkaTemplate<String, NGODto> ngoKafkaTemplate,KafkaTemplate<String, String> kafkaTemplate,
                           NGODataRepo ngoDataRepo,NGODocumentDataRepo ngoDocumentDataRepo){
        this.restTemplate=restTemplate;
        this.longKafkaTemplate=longKafkaTemplate;
        this.ngoKafkaTemplate=ngoKafkaTemplate;
        this.kafkaTemplate=kafkaTemplate;
        this.ngoDataRepo=ngoDataRepo;
        this.ngoDocumentDataRepo=ngoDocumentDataRepo;
    }
    @Value("${ngo.service.url}")
    private String ngoServiceUrl;

    // 🔹 Fetch NGO details by ID
    public NGODto getNGODetails(Long ngoId) {
        return restTemplate.getForObject(
                ngoServiceUrl + "/api/ngos/" + ngoId,
                NGODto.class
        );
    }

    // NGOs that completed profile and submitted for admin review
    public List<NGODto> getPendingNGOs() {
        NGODto[] underReview = restTemplate.getForObject(
                ngoServiceUrl + "/api/ngos/status/UNDER_REVIEW",
                NGODto[].class
        );
        if (underReview == null || underReview.length == 0) {
            return List.of();
        }
        return Arrays.asList(underReview);
    }


    // 🔹 Admin actions (publish events)


    @KafkaListener(topics = "ngo-save-request", groupId = "admin-service-group")
    public void processNGODeleteRequest(NGODto ngoDto) {

        NGOData ngoData = new NGOData();
        ngoData.setId(ngoDto.getId()); // coming from original NGO service
        ngoData.setNgoStatus(ngoDto.getNgoStatus());
        ngoData.setAddress(ngoDto.getAddress());
        ngoData.setPhoneNumber(ngoDto.getPhoneNumber());
        ngoData.setContactEmail(ngoDto.getContactEmail());

        Set<NGODocumentData> newDocs = new HashSet<>();
        if (ngoDto.getDocuments() != null) {
            for (NGODocumentDto ng : ngoDto.getDocuments()) {
                NGODocumentData doc = new NGODocumentData();
                doc.setId(ng.getId());
                doc.setFileName(ng.getFileName());
                doc.setDocumentData(ng.getDocumentData());
                doc.setNgo(ngoData);
                newDocs.add(doc);
            }
        }
        ngoData.setDocuments(newDocs);

        ngoData.setImages(ngoDto.getImages() != null ? new HashSet<>(ngoDto.getImages()) : new HashSet<>());


        ngoDataRepo.save(ngoData);

    }

    public void approveDelete(Long id){
        Optional<NGOData> ngoData=ngoDataRepo.findById(id);
        if(ngoData.isPresent()){
            NGOData n=ngoData.get();
            longKafkaTemplate.send("ngo-delete-approved", n.getId());
            ngoDataRepo.deleteById(id);
        }

    }
    public void rejectDelete(Long id){
        Optional<NGOData> ngoData=ngoDataRepo.findById(id);
        if(ngoData.isPresent()){
            NGOData n=ngoData.get();
            longKafkaTemplate.send("ngo-delete-rejected", n.getId());
            ngoDataRepo.deleteById(id);
        }

    }

    public List<NGODto> getNGOsToApprove(){
        List<NGODto> ngoDtos=new ArrayList<>();
        List<NGOData> ngoDataList=ngoDataRepo.findAll();
        for(int i=0;i<ngoDataList.size();i++){
            NGOData ngoData=ngoDataList.get(i);
            NGODto ngoDto=new NGODto();
            ngoDto.setId(ngoData.getUserId());
            ngoDto.setDescription(ngoData.getDescription());
            ngoDto.setNgoStatus(ngoData.getNgoStatus());
            ngoDto.setAddress(ngoData.getAddress());
            Set<NGODocumentDto> docs=new HashSet<>();
            for(NGODocumentData ng:ngoData.getDocuments()){
                NGODocumentDto ngoDocumentDto=new NGODocumentDto();
                ngoDocumentDto.setId(ng.getId());
                ngoDocumentDto.setFileName(ng.getFileName());
                ngoDocumentDto.setDocumentData(ng.getDocumentData());
                docs.add(ngoDocumentDto);
            }
            ngoDto.setDocuments(docs);
            ngoDto.setImages(ngoData.getImages());
            ngoDtos.add(ngoDto);

        }
        return ngoDtos;
    }
  /** Updates status in ngo-service (source of truth for the review queue). */
    private void applyNgoStatus(Long ngoId, String status) {
        kafkaTemplate.send("ngo-status-events", status + ":" + ngoId);
        restTemplate.postForObject(
                ngoServiceUrl + "/api/ngos/" + ngoId + "/admin/status?status=" + status,
                null,
                NGODto.class
        );
        ngoDataRepo.findById(ngoId).ifPresent(legacy -> ngoDataRepo.deleteById(ngoId));
    }

    public void approveNGO(Long ngoId) {
        applyNgoStatus(ngoId, "APPROVED");
    }

    public void rejectNGO(Long ngoId) {
        applyNgoStatus(ngoId, "REJECTED");
    }

    public void suspendNGO(Long ngoId) {
        applyNgoStatus(ngoId, "SUSPENDED");
    }

    public void deactivateNGO(Long ngoId) {
        applyNgoStatus(ngoId, "DEACTIVATED");
    }

    public byte[] getDocumentBytes(Long ngoId, Long documentId) {
        return restTemplate.getForObject(
                ngoServiceUrl + "/api/ngos/" + ngoId + "/documents/" + documentId + "/download",
                byte[].class
        );
    }


}
