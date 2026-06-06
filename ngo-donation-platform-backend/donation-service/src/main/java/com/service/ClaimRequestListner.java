package com.service;

import com.dto.ClaimRequestDTO;
import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClaimRequestListner {
    private final ClaimRequestService claimRequestService;
    private final KafkaTemplate<String,ClaimRequestDTO> claimRequestTemplate;
    private final KafkaTemplate<String, ClaimRequestDTO> claimAcceptTemplate;
    public ClaimRequestListner(ClaimRequestService claimRequestService,
                               KafkaTemplate<String,ClaimRequestDTO> claimRequestTemplate,
                               KafkaTemplate<String, ClaimRequestDTO> claimAcceptTemplate){
        this.claimRequestService=claimRequestService;
        this.claimRequestTemplate=claimRequestTemplate;
        this.claimAcceptTemplate=claimAcceptTemplate;
    }
    @KafkaListener(topics = "claim-create-request", groupId = "donation-service-group")
    public void claimRequest(ClaimRequestDTO claimRequestDTO){
        ClaimRequest cr = new ClaimRequest();
        String status = claimRequestDTO.getStatus();
        if (status == null || status.isBlank()) {
            cr.setStatus(DonationStatus.PENDING);
        } else {
            cr.setStatus(DonationStatus.valueOf(status.toUpperCase()));
        }
        cr.setDonationId(claimRequestDTO.getDonationId());
        cr.setNgoId(claimRequestDTO.getNgoId());
        claimRequestService.createClaimRequest(cr);

        claimRequestDTO.setStatus(cr.getStatus().name());
        claimRequestTemplate.send("approve-claim-request", claimRequestDTO);
    }
    @KafkaListener(topics = "claim-request-approved", groupId = "donation-service-group")
    public void approvedClaim(Long id){
        claimRequestService.approvedClaim(id);
        ClaimRequest claimRequest=claimRequestService.getClaimById(id);
        ClaimRequestDTO claimRequestDTO=new ClaimRequestDTO();
        claimRequestDTO.setId(claimRequest.getId());
        claimRequestDTO.setDonationId(claimRequest.getDonationId());
        claimRequestDTO.setStatus(claimRequest.getStatus().toString());
        claimRequestDTO.setNgoId(claimRequest.getNgoId());

        claimAcceptTemplate.send("claim-accepted-event",claimRequestDTO);
    }
}
