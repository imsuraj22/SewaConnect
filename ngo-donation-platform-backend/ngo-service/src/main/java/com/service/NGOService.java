package com.service;

import com.entity.NGO;
import com.entity.NGODocument;
import com.entity.NGOStatus;
import com.entity.Package;
import com.repository.NGORepository;
import com.repository.NGODocumentRepository;
import com.repository.PackageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;
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
    private final PackageRepository packageRepository;

    public NGOService(NGORepository ngoRepository, NGODocumentRepository documentRepository,
                      PackageRepository packageRepository) {
        this.ngoRepository = ngoRepository;
        this.documentRepository = documentRepository;
        this.packageRepository=packageRepository;
    }

    public NGO registerNGO(Long userId) {
        NGO ngo = new NGO();
        ngo.setUserId(userId);
        ngo.setStatus(NGOStatus.PENDING);  // default status until admin approves// profile is soft-active but not approved
        return ngoRepository.save(ngo);
    }


    // ✅ Upload one or more documents
    public Set<NGODocument> uploadDocuments(Long ngoId, List<MultipartFile> files) throws IOException {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

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

    // ✅ Get all docs for NGO
    public Set<NGODocument> getDocuments(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        return ngo.getDocuments();
    }

    // ✅ Update NGO details (only allowed fields)
    // ✅ NGOService
    public NGO updateNGO(Long ngoId, NGO updatedData) {
        NGO existing = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

        if (updatedData.getDescription() != null) {
            existing.setDescription(updatedData.getDescription());
        }
        if (updatedData.getAddress() != null) {
            existing.setAddress(updatedData.getAddress());
        }
        if (updatedData.getImages() != null && !updatedData.getImages().isEmpty()) {
            existing.setImages(updatedData.getImages());
        }

        return ngoRepository.save(existing);
    }


    // ✅ Get NGO by ID
    public NGO getNGOById(Long ngoId) {
        return ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
    }

    // ✅ Get all APPROVED NGOs (only visible ones to donors)
    public List<NGO> getAllNGOs() {
        return ngoRepository.findAll()
                .stream()
                .filter(ngo -> ngo.getStatus() == NGOStatus.APPROVED)
                .toList();
    }

    // ✅ Admin actions
    public NGO approveNGO(Long ngoId) {
        NGO ngo = getNGOById(ngoId);
        ngo.setStatus(NGOStatus.APPROVED);
        return ngoRepository.save(ngo);
    }

    public NGO rejectNGO(Long ngoId) {
        NGO ngo = getNGOById(ngoId);
        ngo.setStatus(NGOStatus.REJECTED);
        return ngoRepository.save(ngo);
    }

    public NGO suspendNGO(Long ngoId) {
        NGO ngo = getNGOById(ngoId);
        ngo.setStatus(NGOStatus.SUSPENDED);
        return ngoRepository.save(ngo);
    }
    public void deactivateNGO(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

        ngo.setStatus(NGOStatus.DEACTIVATED); // ✅ soft delete by status
        ngoRepository.save(ngo);
    }


    //package related
    // Create new package for an NGO
    public Package createPackage(Long ngoId, Package pkg) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found"));

        pkg.setNgo(ngo);
        packageRepository.save(pkg);
        ngoRepository.save(ngo);

        return pkg;
    }

    // Get package by ID from NGO's list
    public Package getPackageById(Long packageId) {
        return packageRepository.findById(packageId).orElse(null);
    }


    // Get all packages for an NGO
    public List<Package> getAllPackagesForNGO(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found"));

        return packageRepository.findByNgoId(ngoId);
    }


}
