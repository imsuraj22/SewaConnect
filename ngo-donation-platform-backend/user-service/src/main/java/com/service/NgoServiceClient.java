package com.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

/**
 * Calls ngo-service internal register API with JSON (empty body), not form-urlencoded.
 */
@Component
public class NgoServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ngo.service.url}")
    private String ngoServiceUrl;

    @Value("${app.internal-api-key}")
    private String internalApiKey;

    public NgoServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void registerNgoStub(Long userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Internal-Api-Key", internalApiKey);
        HttpEntity<java.util.Map<String, Object>> request =
                new HttpEntity<>(Collections.emptyMap(), headers);
        restTemplate.postForObject(
                ngoServiceUrl + "/api/ngos/register/" + userId,
                request,
                Void.class
        );
    }
}
