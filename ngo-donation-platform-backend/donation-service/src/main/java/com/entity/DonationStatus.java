package com.entity;

public enum DonationStatus {
    PENDING,      // Created but not yet confirmed (payment or item handover not done)
    WITHDRAWN,
    NOT_AVAILABLE,
    ACCEPTED// Donor or NGO cancelled
}
