package com.service;

import com.dto.ClaimRequestDTO;
import com.entity.ClaimRequestData;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class UserEventListener {

    private final UserService userService;
    private final ClaimRequestDataService claimRequestDataService;
    public UserEventListener(UserService userService,ClaimRequestDataService claimRequestDataService){
        this.userService=userService;
        this.claimRequestDataService=claimRequestDataService;
    }

    @KafkaListener(topics = "user-delete-approved", groupId = "donation-group")
    public void handleUserDeleteApproval(Long userId) {
        Long id = Long.valueOf(userId);
        userService.deleteById(id);
        //send user mail
    }

    // Handle admin rejection
    @KafkaListener(topics = "user-delete-rejected", groupId = "donation-group")
    public void handleUserDeleteRejection(Long userId) {
        System.out.println("User deletion rejected for id: " + userId);
        //send user a mail
    }
    @KafkaListener(topics = "approve-claim-request", groupId = "donation-group")
    public void handleClaimApproval(ClaimRequestDTO claimRequestDTO){
        ClaimRequestData claimRequestData=new ClaimRequestData();
        claimRequestData.setId(claimRequestDTO.getId());
        claimRequestData.setStatus(claimRequestDTO.getStatus());
        claimRequestData.setCreatedAt(claimRequestDTO.getCreatedAt());
        claimRequestData.setNgoId(claimRequestDTO.getNgoId());
        claimRequestDataService.createClaimData(claimRequestData);


    }
}
