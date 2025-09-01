package com.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
public class NGO {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;



    @Column(nullable = false)
    private String password;

    @Column(unique = true, nullable = false)
    private String phoneNumber;


    private String address;

    @Column( length = 1000)
    private String description;

    @Column(nullable = false)
    private boolean active = true;



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
    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {

        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public NGOStatus getStatus() {
        return status;
    }

    public void setStatus(NGOStatus status) {
        this.status = status;
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


    public Set<NGODocument> getDocuments() {
        return documents;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setDocuments(Set<NGODocument> documents) {
        this.documents = documents;
    }


    public Set<byte[]> getImages() {
        return images;
    }
    public void setImages(Set<byte[]> images) {
        this.images = images;
    }

    public Double getLocationLat() {
        return locationLat;
    }
    public void setLocationLat(Double locationLat) {
        this.locationLat = locationLat;
    }

    public Double getLocationLng() {
        return locationLng;
    }
    public void setLocationLng(Double locationLng) {
        this.locationLng = locationLng;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
