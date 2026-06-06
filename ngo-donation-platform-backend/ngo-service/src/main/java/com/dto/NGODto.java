package com.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;
import java.util.Set;

public class NGODto {

    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String description;
    private String phoneNumber;
    private String contactEmail;
    private String ngoStatus;
    private Set<NGODocumentDto> documents;
    private Set<byte[]> images;
    private int profileCompletionPercent;
    private boolean profileComplete;
    private List<String> missingProfileFields;
    private boolean hasOrganizationImage;

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

    public String getNgoStatus() {
        return ngoStatus;
    }

    public void setNgoStatus(String ngoStatus) {
        this.ngoStatus = ngoStatus;
    }

    public Set<NGODocumentDto> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<NGODocumentDto> documents) {
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

    public int getProfileCompletionPercent() {
        return profileCompletionPercent;
    }

    public void setProfileCompletionPercent(int profileCompletionPercent) {
        this.profileCompletionPercent = profileCompletionPercent;
    }

    public boolean isProfileComplete() {
        return profileComplete;
    }

    public void setProfileComplete(boolean profileComplete) {
        this.profileComplete = profileComplete;
    }

    public List<String> getMissingProfileFields() {
        return missingProfileFields;
    }

    public void setMissingProfileFields(List<String> missingProfileFields) {
        this.missingProfileFields = missingProfileFields;
    }

    public boolean isHasOrganizationImage() {
        return hasOrganizationImage;
    }

    public void setHasOrganizationImage(boolean hasOrganizationImage) {
        this.hasOrganizationImage = hasOrganizationImage;
    }
}
