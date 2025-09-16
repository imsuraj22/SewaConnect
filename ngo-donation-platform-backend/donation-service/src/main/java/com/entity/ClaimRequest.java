    package com.entity;

    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;

    import java.time.LocalDate;

    @Entity
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

        public long getId() {
            return id;
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

        public DonationStatus getStatus() {
            return status;
        }

        public void setStatus(DonationStatus status) {
            this.status = status;
        }

        public LocalDate getCreatedAt() {
            return createdAt;
        }

        public LocalDate getUpdatedAt() {
            return updatedAt;
        }

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
