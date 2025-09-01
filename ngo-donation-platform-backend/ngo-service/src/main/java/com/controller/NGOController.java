package com.controller;

import com.entity.NGO;
import com.entity.NGODocument;
import com.service.NGOService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/ngos")
public class NGOController {

    private final NGOService ngoService;

    public NGOController(NGOService ngoService) {
        this.ngoService = ngoService;
    }

    // ✅ Register NGO (minimal fields: name, email, phone, password)
    @PostMapping("/register")
    public ResponseEntity<NGO> registerNGO(@RequestBody NGO ngo) {
        NGO saved = ngoService.registerNGO(ngo);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ Add one or multiple documents
    @PostMapping("/{ngoId}/documents")
    public ResponseEntity<Set<NGODocument>> uploadDocuments(
            @PathVariable Long ngoId,
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        Set<NGODocument> uploadedDocs = ngoService.uploadDocuments(ngoId, files);
        return ResponseEntity.ok(uploadedDocs);
    }

    // ✅ Update NGO profile
    @PutMapping("/{ngoId}")
    public ResponseEntity<NGO> updateNGO(@PathVariable Long ngoId, @RequestBody NGO updatedData) {
        NGO updatedNGO = ngoService.updateNGO(ngoId, updatedData);
        return ResponseEntity.ok(updatedNGO);
    }

    // ✅ Soft Delete NGO
    @DeleteMapping("/{ngoId}")
    public ResponseEntity<String> deleteNGO(@PathVariable Long ngoId) {
        ngoService.deleteNGO(ngoId);
        return ResponseEntity.ok("NGO soft deleted successfully.");
    }

    // ✅ Get NGO by ID
    @GetMapping("/{ngoId}")
    public ResponseEntity<NGO> getNGO(@PathVariable Long ngoId) {
        NGO ngo = ngoService.getNGOById(ngoId);
        return ResponseEntity.ok(ngo);
    }

    // ✅ Get all NGOs
    @GetMapping
    public ResponseEntity<List<NGO>> getAllNGOs() {
        return ResponseEntity.ok(ngoService.getAllNGOs());
    }

}
