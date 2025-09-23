    package com.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.Data;

    import java.time.LocalDate;

    @Entity
    @Data
    public class ClaimRequest {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long id;

        @NotNull
        private long donationId;

        @NotNull
        private long ngoId;

        @Enumerated(EnumType.STRING)  // Store enum as text in DB (PENDING, ACCEPTED, REJECTED)
        @NotNull
        private DonationStatus status;

        private LocalDate createdAt;
        private LocalDate updatedAt;


        // ----- Getters & Setters -----


        // Auto-set createdAt when inserting new record
        @PrePersist
        public void onCreate() {
            this.createdAt = LocalDate.now();
            this.updatedAt = LocalDate.now();
        }

        // Auto-update updatedAt when updating record
        @PreUpdate
        public void onUpdate() {
            this.updatedAt = LocalDate.now();
        }
    }
