package com.service;

import com.entity.Donation;
import com.entity.DonationImage;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.repository.DonationImageRepo;
import com.repository.DonationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationImageRepo donationImageRepo;



    public Donation createDonation(Donation dto, MultipartFile[] images) throws IOException {
        // 1. Save Donation
        Donation donation = new Donation();
        donation.setDonorId(dto.getDonorId());
        donation.setDonationType(dto.getDonationType());
        donation.setDonationStatus(dto.getDonationStatus());
        donation.setNgoId(dto.getNgoId());
        donation.setAmount(dto.getAmount());
        donation.setCurrency(dto.getCurrency());


        // 2. Save Images linked to donation
        for (MultipartFile file : images) {
            DonationImage donationImage = new DonationImage();
            donationImage.setImageData(file.getBytes());
            donationImage.setDonation(donation); // <-- links image to donation
            donationImageRepo.save(donationImage);
        }

        return donation;
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
