package com.service;

import com.dto.ClaimRequestDTO;
import com.entity.ClaimRequest;
import com.entity.DonationStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClaimRequestListner {
    private final ClaimRequestService claimRequestService;
    private final KafkaTemplate<String,ClaimRequestDTO> claimRequestTemplate;
    public ClaimRequestListner(ClaimRequestService claimRequestService,
                               KafkaTemplate<String,ClaimRequestDTO> claimRequestTemplate){
        this.claimRequestService=claimRequestService;
        this.claimRequestTemplate=claimRequestTemplate;
    }
    @KafkaListener(topics = "create-claim-request", groupId = "donation-group")
    public void claimRequest(ClaimRequestDTO claimRequestDTO){
        ClaimRequest cr=new ClaimRequest();
        cr.setStatus(DonationStatus.valueOf(claimRequestDTO.getStatus().toUpperCase()));
        cr.setDonationId(claimRequestDTO.getDonationId());
        cr.setNgoId(claimRequestDTO.getNgoId());
        claimRequestService.createClaimRequest(cr);

        claimRequestTemplate.send("approve-claim-request",claimRequestDTO);
    }
    @KafkaListener(topics = "claim-request-approved", groupId = "donation-group")
    public void approvedClaim(Long id){
        claimRequestService.approvedClaim(id);
    }
}
