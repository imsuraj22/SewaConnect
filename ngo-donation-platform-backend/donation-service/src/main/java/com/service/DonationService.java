package com.service;

import com.entity.Donation;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;

    public Donation createDonation(Donation donation) {
        return donationRepository.save(donation);
    }

    public Donation getDonationById(Long id) throws EntityNotFoundException {
        return donationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Donation with id " + id + " not found"));
    }

    public List<Donation> getDonationsByDonor(Long donorId) {
        return donationRepository.findByDonorId(donorId);
    }

    public List<Donation> getDonationsByNgo(Long ngoId) {
        return donationRepository.findByNgoId(ngoId);
    }

    public Donation updateDonationStatus(Long donationId, DonationStatus status) throws EntityNotFoundException {
        Donation donation = getDonationById(donationId);
        donation.setDonationStatus(status);
        return donationRepository.save(donation);
    }

    public void deleteDonation(Long donationId) {
        donationRepository.deleteById(donationId);
    }
}
