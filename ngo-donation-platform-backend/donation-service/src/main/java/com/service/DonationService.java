package com.service;

import com.entity.Donation;
import com.entity.DonationImage;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.repository.DonationImageRepo;
import com.repository.DonationRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
public class DonationService {

    private final DonationRepository donationRepository;
    private final DonationImageRepo donationImageRepo;

    public DonationService(DonationRepository donationRepository, DonationImageRepo donationImageRepo) {
        this.donationRepository = donationRepository;
        this.donationImageRepo = donationImageRepo;
    }


    public Donation createDonation(Donation dto, MultipartFile[] images) throws IOException {
        Donation donation = new Donation();
        donation.setDonorId(dto.getDonorId());
        donation.setDonationType(dto.getDonationType());
        donation.setDonationStatus(
                dto.getDonationStatus() != null ? dto.getDonationStatus() : DonationStatus.PENDING);
        donation.setNgoId(dto.getNgoId());
        donation.setAmount(dto.getAmount());
        donation.setCurrency(dto.getCurrency());
        donation.setPackageId(dto.getPackageId());
        donation.setItemName(dto.getItemName());
        donation.setItemDescription(dto.getItemDescription());

        donation = donationRepository.save(donation);

        if (images != null) {
            for (MultipartFile file : images) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                DonationImage donationImage = new DonationImage();
                donationImage.setImageData(file.getBytes());
                donationImage.setDonation(donation);
                donationImageRepo.save(donationImage);
            }
        }

        return donationRepository.findById(donation.getId()).orElse(donation);
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

    public void setDonationStatus(Long id,DonationStatus status){
        Optional<Donation> donation=donationRepository.findById(id);
        if(donation.isPresent()){
            Donation d=donation.get();
            d.setDonationStatus(status);
            donationRepository.save(d);
        }
    }
}
