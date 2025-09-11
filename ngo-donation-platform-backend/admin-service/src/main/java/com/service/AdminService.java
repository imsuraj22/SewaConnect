package com.service;

import com.dto.NGODto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class AdminService {

    private final RestTemplate restTemplate;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Value("${ngo.service.url}") // e.g. http://localhost:8081
    private String ngoServiceUrl;

    public AdminService(RestTemplate restTemplate, KafkaTemplate<String, String> kafkaTemplate) {
        this.restTemplate = restTemplate;
        this.kafkaTemplate = kafkaTemplate;
    }

    // 🔹 Fetch NGO details by ID
    public NGODto getNGODetails(Long ngoId) {
        return restTemplate.getForObject(
                ngoServiceUrl + "/ngos/" + ngoId,
                NGODto.class
        );
    }

    // 🔹 Fetch all pending NGOs
    public List<NGODto> getPendingNGOs() {
        NGODto[] result = restTemplate.getForObject(
                ngoServiceUrl + "/ngos/status/PENDING",
                NGODto[].class
        );
        return Arrays.asList(result);
    }

    // 🔹 Admin actions (publish events)
    public void approveNGO(Long ngoId) {
        kafkaTemplate.send("ngo-status-events", "APPROVED:" + ngoId);
    }

    public void rejectNGO(Long ngoId) {
        kafkaTemplate.send("ngo-status-events", "REJECTED:" + ngoId);
    }

    public void suspendNGO(Long ngoId) {
        kafkaTemplate.send("ngo-status-events", "SUSPENDED:" + ngoId);
    }

    public void deactivateNGO(Long ngoId) {
        kafkaTemplate.send("ngo-status-events", "DEACTIVATED:" + ngoId);
    }
}
