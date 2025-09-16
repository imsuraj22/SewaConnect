package com.service;

import com.dto.ClaimRequestDTO;
import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class ClaimRequestListner {
    private final ClaimRequestService claimRequestService;
    public ClaimRequestListner(ClaimRequestService claimRequestService){
        this.claimRequestService=claimRequestService;
    }
    @KafkaListener(topics = "create-claim-request", groupId = "donation-group")
    public void claimRequest(ClaimRequestDTO claimRequestDTO){
        ClaimRequest cr=new ClaimRequest();
        cr.setStatus(DonationStatus.valueOf(claimRequestDTO.getStatus().toUpperCase()));
        cr.setDonationId(claimRequestDTO.getDonationId());
        cr.setNgoId(claimRequestDTO.getNgoId());
        claimRequestService.createClaimRequest(cr);
    }
}
