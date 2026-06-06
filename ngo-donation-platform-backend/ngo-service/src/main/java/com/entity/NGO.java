package com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * NGO profile. Signup creates a minimal stub (userId + PENDING); other fields stay null
 * until completed in NGO workspace. Profile columns must not be unique — legacy DB
 * unique indexes on empty strings blocked multiple stubs.
 */
@Entity
@Table(
        name = "ngo",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_ngo_user_id", columnNames = "user_id")
        }
)
public class NGO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(nullable = true, unique = false, length = 500)
    private String name;

    @Column(nullable = true, unique = false, length = 1000)
    private String address;

    @Column(nullable = true, unique = false, length = 2000)
    private String description;

    @Column(name = "phone_number", nullable = true, unique = false, length = 64)
    private String phoneNumber;

    /** Legacy column name in PostgreSQL is {@code email}. */
    @Column(name = "email", nullable = true, unique = false, length = 255)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private NGOStatus status = NGOStatus.PENDING;

    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NGODocument> documents = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ngo_images", joinColumns = @JoinColumn(name = "ngo_id"))
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "image_data", columnDefinition = "bytea")
    private Set<byte[]> images = new HashSet<>();

    @Column(nullable = true)
    private LocalDateTime createdAt;

    @Column(nullable = true)
    private LocalDateTime updatedAt;

    public NGO() {
    }

    /** Treat blank strings as SQL NULL so legacy UNIQUE constraints are not violated. */
    public static String emptyToNull(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private void normalizeProfileFields() {
        this.name = emptyToNull(this.name);
        this.address = emptyToNull(this.address);
        this.description = emptyToNull(this.description);
        this.phoneNumber = emptyToNull(this.phoneNumber);
        this.contactEmail = emptyToNull(this.contactEmail);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public NGOStatus getStatus() {
        return status;
    }

    public void setStatus(NGOStatus status) {
        this.status = status;
    }

    @JsonIgnore
    public Set<NGODocument> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<NGODocument> documents) {
        this.documents = documents;
    }

    @JsonIgnore
    public Set<byte[]> getImages() {
        return images;
    }

    public void setImages(Set<byte[]> images) {
        this.images = images;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @PrePersist
    public void onCreate() {
        normalizeProfileFields();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        normalizeProfileFields();
        this.updatedAt = LocalDateTime.now();
    }
}
