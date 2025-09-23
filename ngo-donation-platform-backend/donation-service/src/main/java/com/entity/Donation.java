package com.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class Donation {

    @Id

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "donor_id", nullable = false)
    private Long donorId;  // FK → User Service

    @Column(name = "ngo_id")
    private Long ngoId;    // FK → NGO Service (nullable if donor just posts items without choosing NGO)

    @Column
    private Double amount; // For money donations or package payments

    @Column
    private String currency; // Required if amount != null

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationStatus donationStatus=DonationStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DonationType donationType;  // (DIRECT_MONEY, PACKAGE, ITEM)

    // --- For PACKAGE donation ---
    private Long packageId; // Nullable, only filled when donor selects an NGO package

    // --- For ITEM donation ---
    private String itemName;
    private String itemDescription;

    @OneToMany(mappedBy = "donation", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DonationImage> images = new ArrayList<>();

    private boolean isClaimed=false;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    // --- Lifecycle Hooks ---
    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
        this.donationStatus = DonationStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDate.now();
    }

    // --- Getters & Setters ---


}
