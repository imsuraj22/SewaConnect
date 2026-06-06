package com.dto;

import java.time.LocalDate;

public class ClaimRequestDTO {

    private long id;
    private long donationId;
    private long ngoId;
    private String status;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private boolean claimed;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDonationId() {
        return donationId;
    }

    public void setDonationId(long donationId) {
        this.donationId = donationId;
    }

    public long getNgoId() {
        return ngoId;
    }

    public void setNgoId(long ngoId) {
        this.ngoId = ngoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isClaimed() {
        return claimed;
    }

    public void setClaimed(boolean claimed) {
        this.claimed = claimed;
    }
}
