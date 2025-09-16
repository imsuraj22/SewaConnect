package com.controller;

import com.dto.ClaimRequestDTO;
import com.dto.NGODto;
import com.entity.NGO;
import com.entity.NGODocument;
import com.entity.NGOStatus;
import com.service.NGOService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import com.entity.Package;

@RestController
@RequestMapping("/api/ngos")
public class NGOController {

    private final NGOService ngoService;

    public NGOController(NGOService ngoService) {
        this.ngoService = ngoService;
    }

    // ✅ Register NGO (linked with an existing User by userId)
    @PostMapping("/register/{userId}")
    public ResponseEntity<NGO> registerNGO(
            @PathVariable Long userId) {
        NGO saved = ngoService.registerNGO(userId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ Upload documents (multiple allowed)
    @PostMapping("/{ngoId}/documents")
    public ResponseEntity<Set<NGODocument>> uploadDocuments(
            @PathVariable Long ngoId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        Set<NGODocument> uploadedDocs = ngoService.uploadDocuments(ngoId, files);
        return ResponseEntity.ok(uploadedDocs);
    }

    // ✅ Update NGO profile (address, description, location, images, etc.)
    @PutMapping("/{ngoId}")
    public ResponseEntity<NGO> updateNGO(
            @PathVariable Long ngoId,
            @RequestBody NGO updatedData) {
        NGO updatedNGO = ngoService.updateNGO(ngoId, updatedData);
        return ResponseEntity.ok(updatedNGO);
    }

    // ✅ Soft Delete NGO (mark as DEACTIVATED)
    @DeleteMapping("/{ngoId}")
    public ResponseEntity<String> deleteNGO(@PathVariable Long ngoId) {
        ngoService.deactivateNGO(ngoId);
        return ResponseEntity.ok("NGO has been deactivated successfully.");
    }


//    // ✅ Get NGO by ID
        @GetMapping("/{id}")
        public ResponseEntity<NGODto> getNGO(@PathVariable Long id) {
            return ResponseEntity.ok(ngoService.getNGOById(id));
        }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NGODto>> getNGO(@PathVariable NGOStatus status) {
        return ResponseEntity.ok(ngoService.getNGOByStatus(status));
    }

    @PostMapping("/{ngoId}/packages")
    public ResponseEntity<Package> createPackage(
            @PathVariable Long ngoId,
            @RequestBody Package pkg) {
        return ResponseEntity.ok(ngoService.createPackage(ngoId, pkg));
    }

    // Get all packages for an NGO
    @GetMapping("/{ngoId}/packages")
    public ResponseEntity<List<Package>> getPackages(@PathVariable Long ngoId) {
        return ResponseEntity.ok(ngoService.getAllPackagesForNGO(ngoId));
    }

    @PostMapping("/claim-request/{id}")
    public ResponseEntity<String> createClaimRequest(ClaimRequestDTO claimRequestDTO,@PathVariable Long id){

    }



}
