package com.service;

import com.entity.NGO;
import com.entity.NGODocument;
import com.repository.NGORepository;
import com.repository.NGODocumentRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class NGOService {

    private final NGORepository ngoRepository;
    private final NGODocumentRepository documentRepository;

    public NGOService(NGORepository ngoRepository, NGODocumentRepository documentRepository) {
        this.ngoRepository = ngoRepository;
        this.documentRepository = documentRepository;
    }

    // Save NGO basic info (signup)
    public NGO registerNGO(NGO ngo) {
        return ngoRepository.save(ngo);
    }

    // Upload one or more documents
    public Set<NGODocument> uploadDocuments(Long ngoId, List<MultipartFile> files) throws IOException {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));

        Set<NGODocument> uploadedDocs = new HashSet<>();

        for (MultipartFile file : files) {
            NGODocument doc = new NGODocument();
            doc.setFileName(file.getOriginalFilename());
            doc.setDocumentData(file.getBytes());
            doc.setNgo(ngo);

            uploadedDocs.add(documentRepository.save(doc));
        }

        return uploadedDocs;
    }

    // Get all docs for NGO
    public Set<NGODocument> getDocuments(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new RuntimeException("NGO not found"));
        return ngo.getDocuments();
    }

    public NGO updateNGO(Long ngoId, NGO updatedData) {
        NGO existing = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

        // update only allowed fields
        if (updatedData.getName() != null) {
            existing.setName(updatedData.getName());
        }
        if (updatedData.getEmail() != null) {
            existing.setEmail(updatedData.getEmail());
        }
        if (updatedData.getPhoneNumber() != null) {
            existing.setPhoneNumber(updatedData.getPhoneNumber());
        }
        if (updatedData.getAddress() != null) {
            existing.setAddress(updatedData.getAddress());
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getImages() != null && !updatedData.getImages().isEmpty()) {
            existing.setImages(updatedData.getImages());
        }

        // Save changes
        return ngoRepository.save(existing);
    }
    // ✅ Get NGO by ID
    public NGO getNGOById(Long ngoId) {
        return ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id: " + ngoId));
    }

    // ✅ Get all NGOs
    public List<NGO> getAllNGOs() {
        return ngoRepository.findAll()
                .stream()
                .filter(NGO::isActive) // only return active NGOs
                .toList();
    }

    public void deleteNGO(Long ngoId) {
        NGO existing = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

        existing.setActive(false); // mark inactive
        ngoRepository.save(existing);
    }
}
