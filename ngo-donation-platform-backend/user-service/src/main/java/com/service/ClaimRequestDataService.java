package com.service;

import com.entity.ClaimRequestData;
import com.repository.ClaimRequestDataRepo;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClaimRequestDataService {
    private final ClaimRequestDataRepo claimRequestDataRepo;
    private final KafkaTemplate<String,Long> claimRequestApproveTemplate;
    public ClaimRequestDataService(ClaimRequestDataRepo claimRequestDataRepo,
                                   KafkaTemplate<String,Long> claimRequestApproveTemplate){
        this.claimRequestDataRepo=claimRequestDataRepo;
        this.claimRequestApproveTemplate=claimRequestApproveTemplate;
    }

    public void createClaimData(ClaimRequestData claimRequestData){
        claimRequestDataRepo.save(claimRequestData);
    }

    public void approveClaim(Long id){
        claimRequestApproveTemplate.send("claim-request-approved",id);
        Optional<ClaimRequestData> claimRequestData=claimRequestDataRepo.findById(id);
        if(claimRequestData.isPresent()){
            ClaimRequestData cd=claimRequestData.get();
            deleteByDonationId(cd.getDonationId());
        }

    }
    public void deleteByDonationId(Long id){
        claimRequestDataRepo.deleteByDonationId(id);
    }
}
