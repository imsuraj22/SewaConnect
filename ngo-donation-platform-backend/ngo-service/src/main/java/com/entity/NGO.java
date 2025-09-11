package com.entity;

import jakarta.persistence.*;
import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Pack;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.Data;

@Entity
@Data
public class NGO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId; // comes from User Service
    private String name;
    private String address;
    private String description;

//    public Long getUserId() {
//        return userId;
//    }
//
//    public void setUserId(Long userId) {
//        this.userId = userId;
//    }

    @Enumerated(EnumType.STRING)
    private NGOStatus status = NGOStatus.PENDING;

    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NGODocument> documents = new HashSet<>();


    // ✅ Store raw images in DB (BLOBs)
    @ElementCollection
    @CollectionTable(name = "ngo_images", joinColumns = @JoinColumn(name = "ngo_id"))
    @Column(name = "image_data", columnDefinition = "LONGBLOB")  // Use BLOB/LONGBLOB
    private Set<byte[]> images = new HashSet<>();


    private Double locationLat;
    private Double locationLng;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Getters & Setters
//    public long getId() {
//        return id;
//    }
//
//    public NGOStatus getStatus() {
//        return status;
//    }
//
//    public void setStatus(NGOStatus status) {
//        this.status = status;
//    }
//
//    public String getAddress() {
//        return address;
//    }
//    public void setAddress(String address) {
//        this.address = address;
//    }
//
//    public String getDescription() {
//        return description;
//    }
//    public void setDescription(String description) {
//        this.description = description;
//    }
//
//
//    public Set<NGODocument> getDocuments() {
//        return documents;
//    }
//
//    public void setDocuments(Set<NGODocument> documents) {
//        this.documents = documents;
//    }
//
//
//    public Set<byte[]> getImages() {
//        return images;
//    }
//    public void setImages(Set<byte[]> images) {
//        this.images = images;
//    }
//
//    public Double getLocationLat() {
//        return locationLat;
//    }
//    public void setLocationLat(Double locationLat) {
//        this.locationLat = locationLat;
//    }
//
//    public Double getLocationLng() {
//        return locationLng;
//    }
//    public void setLocationLng(Double locationLng) {
//        this.locationLng = locationLng;
//    }
//
//    public LocalDateTime getCreatedAt() {
//        return createdAt;
//    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

//    public LocalDateTime getUpdatedAt() {
//        return updatedAt;
//    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
