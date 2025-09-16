package com.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClaimRequestDTO {
    private long id;
    private long donationId;
    private long ngoId;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
}
