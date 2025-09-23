package com.entity;

import jakarta.persistence.Entity;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class ClaimRequestData {
    private long id;
    private long donationId;
    private long ngoId;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;

}
