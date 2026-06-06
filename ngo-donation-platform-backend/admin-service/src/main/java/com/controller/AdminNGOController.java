package com.controller;

import com.dto.NGODto;
import com.service.AdminNGOService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ngos")
public class AdminNGOController {

    private final AdminNGOService adminNGOService;

    public AdminNGOController(AdminNGOService adminNGOService) {
        this.adminNGOService = adminNGOService;
    }

    // 🔹 View NGO details
    @GetMapping("/{ngoId}")
    public NGODto getNGODetails(@PathVariable Long ngoId) {
        return adminNGOService.getNGODetails(ngoId);
    }

    // 🔹 List all pending NGOs
    @GetMapping("/pending")
    public List<NGODto> getPendingNGOs() {
        return adminNGOService.getPendingNGOs();
    }

    // 🔹 Approve / Reject / Suspend / Deactivate
    @PostMapping("/{ngoId}/approve")
    public void approveNGO(@PathVariable Long ngoId) {
        adminNGOService.approveNGO(ngoId);
    }

    @GetMapping("/pending-approve")
    public List<NGODto> getToApproveNGOs(){
        return adminNGOService.getNGOsToApprove();
    }

    @PostMapping("/approve-delete/{id}")
    public void approveNGODelete(@PathVariable Long id) {
        adminNGOService.approveDelete(id);
    }

    @PostMapping("/reject-delete/{id}")
    public void rejectNGODelete(@PathVariable Long id) {
        adminNGOService.rejectDelete(id);
    }

    @PostMapping("/{ngoId}/reject")
    public void rejectNGO(@PathVariable Long ngoId) {
        adminNGOService.rejectNGO(ngoId);
    }

    @PostMapping("/{ngoId}/suspend")
    public void suspendNGO(@PathVariable Long ngoId) {
        adminNGOService.suspendNGO(ngoId);
    }

    @PostMapping("/{ngoId}/deactivate")
    public void deactivateNGO(@PathVariable Long ngoId) {
        adminNGOService.deactivateNGO(ngoId);
    }

    @GetMapping("/{ngoId}/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long ngoId,
            @PathVariable Long documentId
    ) {
        byte[] data = adminNGOService.getDocumentBytes(ngoId, documentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"document\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }
}
