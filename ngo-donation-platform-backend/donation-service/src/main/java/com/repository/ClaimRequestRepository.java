package com.repository;

import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ClaimRequestRepository extends JpaRepository<ClaimRequest,Long> {
    List<ClaimRequest> findByNgoId(Long ngoId);

    // All claim requests for a given Donation
    List<ClaimRequest> findByDonationId(Long donationId);

    // Filter by status (PENDING / ACCEPTED / REJECTED)
    List<ClaimRequest> findByStatus(DonationStatus status);

    // All claim requests for NGO filtered by status
    List<ClaimRequest> findByNgoIdAndStatus(Long ngoId, DonationStatus status);

    // If you want to see which NGO claimed a donation and its status
    List<ClaimRequest> findByDonationIdAndNgoId(Long donationId, Long ngoId);
}
