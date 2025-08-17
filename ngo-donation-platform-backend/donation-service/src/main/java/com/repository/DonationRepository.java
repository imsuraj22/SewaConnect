package com.repository;

import com.entity.Donation;
import com.entity.DonationStatus;
import com.entity.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DonationRepository extends JpaRepository<Donation,Long> {
    List<Donation> findByDonorId(Long donorId);

    // Get all donations for a specific NGO
    List<Donation> findByNgoId(Long ngoId);

    // Get all donations by status (e.g., PENDING, COMPLETED)
    List<Donation> findByDonationStatus(DonationStatus donationStatus);

    // Get all donations of a particular type (e.g., MONEY, PACKAGE, ITEM)
    List<Donation> findByDonationType(DonationType donationType);
}
