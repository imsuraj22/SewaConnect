package com.controller;

import com.entity.Donation;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // --- Create Donation ---
    @PostMapping()
    public ResponseEntity<Donation> createDonation(
            @RequestPart("donation") Donation donation,
            @RequestPart("images") MultipartFile[] images) throws IOException {

        Donation savedDonation = donationService.createDonation(donation, images);
        return ResponseEntity.ok(savedDonation);
    }

    // --- Get Donation By Id ---
    @GetMapping("/{id}")
    public ResponseEntity<Donation> getDonationById(@PathVariable Long id) throws EntityNotFoundException {
        return ResponseEntity.ok(donationService.getDonationById(id));
    }

    // --- Get Donations By Donor ---
    @GetMapping("/donor/{donorId}")
    public ResponseEntity<List<Donation>> getDonationsByDonor(@PathVariable Long donorId) {
        return ResponseEntity.ok(donationService.getDonationsByDonor(donorId));
    }

    // --- Get Donations By NGO ---
    @GetMapping("/ngo/{ngoId}")
    public ResponseEntity<List<Donation>> getDonationsByNgo(@PathVariable Long ngoId) {
        return ResponseEntity.ok(donationService.getDonationsByNgo(ngoId));
    }

    // --- Update Donation Status ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<Donation> updateDonationStatus(
            @PathVariable Long id,
            @RequestParam DonationStatus status
    ) throws EntityNotFoundException {
        Donation updatedDonation = donationService.updateDonationStatus(id, status);
        return ResponseEntity.ok(updatedDonation);
    }

    // --- Delete Donation ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDonation(@PathVariable Long id) {
        donationService.deleteDonation(id);
        return ResponseEntity.noContent().build();
    }
}
