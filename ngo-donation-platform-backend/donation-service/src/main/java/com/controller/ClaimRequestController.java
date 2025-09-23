package com.controller;

import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.service.ClaimRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/claims")
public class ClaimRequestController {

    private final ClaimRequestService claimRequestService;

    public ClaimRequestController(ClaimRequestService claimRequestService){
        this.claimRequestService=claimRequestService;
    }

    // Create a new claim by ngo
    @PostMapping
    public ResponseEntity<ClaimRequest> createClaim(@RequestBody ClaimRequest claimRequest) {
        return ResponseEntity.ok(claimRequestService.createClaimRequest(claimRequest));
    }

    // Get all claims admin
    @GetMapping
    public ResponseEntity<List<ClaimRequest>> getAllClaims() {
        return ResponseEntity.ok(claimRequestService.getAllClaims());
    }

    // user will need this method
    @GetMapping("/donation/{donationId}")
    public ResponseEntity<List<ClaimRequest>> getClaimsByDonation(@PathVariable Long donationId) {
        return ResponseEntity.ok(claimRequestService.getClaimsByDonationId(donationId));
    }

    // ngo will need this method
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<ClaimRequest>> getClaimsByNgo(@PathVariable Long ngoId) {
        return ResponseEntity.ok(claimRequestService.getClaimsByNgoId(ngoId));
    }

    // for user via kafka
    @PutMapping("/{id}/status")
    public ResponseEntity<ClaimRequest> updateClaimStatus(
            @PathVariable Long id,
            @RequestParam DonationStatus status) throws EntityNotFoundException {
        return ResponseEntity.ok(claimRequestService.updateClaimStatus(id, status));
    }

    //for ngo
    @PutMapping("/{id}/withdraw")
    public ResponseEntity<ClaimRequest> withdrawClaim(
            @PathVariable Long id,
            @RequestParam Long ngoId) throws EntityNotFoundException {
        return ResponseEntity.ok(claimRequestService.withdrawClaim(id, ngoId));
    }

    // admin
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteClaim(@PathVariable Long id) {
        claimRequestService.deleteClaim(id);
        return ResponseEntity.ok("Claim deleted successfully");
    }
}
