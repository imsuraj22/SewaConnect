package com.service;

import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import com.exceptions.EntityNotFoundException;
import com.repository.ClaimRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ClaimRequestService {

    private final ClaimRequestRepository claimRequestRepository;
    private final DonationService donationService;


    public ClaimRequestService(ClaimRequestRepository claimRequestRepository,DonationService donationService){
        this.claimRequestRepository=claimRequestRepository;
        this.donationService=donationService;
    }

    // Create a new claim request
    public ClaimRequest createClaimRequest(ClaimRequest claimRequest) {
        claimRequest.setStatus(DonationStatus.PENDING); // default when NGO requests
        return claimRequestRepository.save(claimRequest);
    }

    // Get all claims
    public List<ClaimRequest> getAllClaims() {
        return claimRequestRepository.findAll();
    }

    // Get claims by donationId
    public List<ClaimRequest> getClaimsByDonationId(Long donationId) {
        return claimRequestRepository.findByDonationId(donationId);
    }

    // Get claims by NGO id
    public List<ClaimRequest> getClaimsByNgoId(Long ngoId) {
        return claimRequestRepository.findByNgoId(ngoId);
    }

    // Update claim status (Donor accepts/rejects)
    // Update claim status (Donor accepts/rejects)
    @Transactional
    public ClaimRequest updateClaimStatus(Long id, DonationStatus status) throws EntityNotFoundException {
        ClaimRequest claim = claimRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClaimRequest with id " + id + " not found"));

        // If donor accepts this claim
        if (status == DonationStatus.ACCEPTED) {
            claim.setStatus(DonationStatus.ACCEPTED);
            claimRequestRepository.save(claim);

            // Mark all other pending claims for this donation as NOT_AVAILABLE
            List<ClaimRequest> otherClaims = claimRequestRepository.findByDonationId(claim.getDonationId());
            for (ClaimRequest other : otherClaims) {
                if (other.getId()!=(claim.getId()) &&
                        other.getStatus() == DonationStatus.PENDING) {
                    other.setStatus(DonationStatus.NOT_AVAILABLE);
                    claimRequestRepository.save(other);
                }
            }
            return claim;
        }

        // If donor rejects → just mark rejected
    //        if (status == DonationStatus.REJECTED) {
    //            claim.setStatus(DonationStatus.REJECTED);
    //        }

        return claimRequestRepository.save(claim);
    }


    public ClaimRequest withdrawClaim(Long id, Long ngoId) throws EntityNotFoundException {
        ClaimRequest claim = claimRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ClaimRequest with id " + id + " not found"));

        if (claim.getNgoId()!=(ngoId)) {
            throw new IllegalArgumentException("NGO is not allowed to withdraw this claim");
        }

        claim.setStatus(DonationStatus.WITHDRAWN); // or create a new enum WITHDRAWN
        return claimRequestRepository.save(claim);
    }

    // Delete claim
    public void deleteClaim(Long id) {
        claimRequestRepository.deleteById(id);
    }

    public void approvedClaim(Long id){
        Optional<ClaimRequest> cl=claimRequestRepository.findById(id);
        if(cl.isPresent()){
            ClaimRequest claimRequest=cl.get();
            long did=claimRequest.getDonationId();
            donationService.setDonationStatus(id,DonationStatus.ACCEPTED);

        }

    }

}
