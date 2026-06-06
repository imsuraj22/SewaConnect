package com.entity;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class NGOData {

    @Id
    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private String contactEmail;
    private String ngoStatus;

    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NGODocumentData> documents = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ngo_images", joinColumns = @JoinColumn(name = "ngo_id"))
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private Set<byte[]> images = new HashSet<>();

    public NGOData() {
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

    public String getNgoStatus() {
        return ngoStatus;
    }

    public void setNgoStatus(String ngoStatus) {
        this.ngoStatus = ngoStatus;
    }

    public Set<NGODocumentData> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<NGODocumentData> documents) {
        this.documents = documents;
    }

    public Set<byte[]> getImages() {
        return images;
    }

    public void setImages(Set<byte[]> images) {
        this.images = images;
    }

}
