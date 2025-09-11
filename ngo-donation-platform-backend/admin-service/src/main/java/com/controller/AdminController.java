package com.controller;

import com.dto.NGODto;
import com.service.AdminService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ngos")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 🔹 View NGO details
    @GetMapping("/{ngoId}")
    public NGODto getNGODetails(@PathVariable Long ngoId) {
        return adminService.getNGODetails(ngoId);
    }

    // 🔹 List all pending NGOs
    @GetMapping("/pending")
    public List<NGODto> getPendingNGOs() {
        return adminService.getPendingNGOs();
    }

    // 🔹 Approve / Reject / Suspend / Deactivate
    @PostMapping("/{ngoId}/approve")
    public void approveNGO(@PathVariable Long ngoId) {
        adminService.approveNGO(ngoId);
    }

    @PostMapping("/{ngoId}/reject")
    public void rejectNGO(@PathVariable Long ngoId) {
        adminService.rejectNGO(ngoId);
    }

    @PostMapping("/{ngoId}/suspend")
    public void suspendNGO(@PathVariable Long ngoId) {
        adminService.suspendNGO(ngoId);
    }

    @PostMapping("/{ngoId}/deactivate")
    public void deactivateNGO(@PathVariable Long ngoId) {
        adminService.deactivateNGO(ngoId);
    }
}
