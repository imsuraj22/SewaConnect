package com.entity;

public enum DonationStatus {
    PENDING,      // Created but not yet confirmed (payment or item handover not done)
    COMPLETED,    // Donation successfully fulfilled
    FAILED,       // Payment failure / pickup failed
    CANCELLED     // Donor or NGO cancelled
}
