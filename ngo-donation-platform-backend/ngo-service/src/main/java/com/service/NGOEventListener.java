package com.service;

import com.entity.NGO;
import com.entity.NGOStatus;
import com.exceptions.EntityNotFoundException;
import com.repository.NGORepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NGOEventListener {

    private final NGORepository ngoRepository;
    private final NGOService ngoService;
    public NGOEventListener(NGORepository ngoRepository,NGOService ngoService){
        this.ngoRepository=ngoRepository;
        this.ngoService=ngoService;
    }
    @KafkaListener(topics = "ngo-status-events", groupId = "donation-group")
    public void listen(String message) {
        // message = "APPROVED:123"
        String[] parts = message.split(":");
        String status = parts[0];
        Long ngoId = Long.parseLong(parts[1]);

        // Fetch NGO by ID and update status
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found"));

        ngo.setStatus(NGOStatus.valueOf(status));
        ngoRepository.save(ngo);
    }


    @KafkaListener(topics = "ngo-delete-approved", groupId = "donation-group")
    public void handleNGODeleteApproval(Long ngoId) {
        Long id = Long.valueOf(ngoId);
        ngoService.deleteById(id);

        //send email
    }

    @KafkaListener(topics = "ngo-delete-rejected", groupId = "donation-group")
    public void handleApproveNGO(Long ngoId) {
        System.out.println("NGO deletion rejected for id: " + ngoId);
        //send email
    }



}


