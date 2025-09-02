package com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private NGO ngo;


    @NotNull
    private String title;

    @NotNull
    private Double amount;

    // Each package can have multiple items
    @ElementCollection
    @CollectionTable(name = "package_items", joinColumns = @JoinColumn(name = "package_id"))
    private List<PackageItem> items;

    // Each package can have multiple images
    @OneToMany(mappedBy = "pkg", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PackageImage> images;

    private LocalDate createdAt;
    private LocalDate updatedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDate.now();
        updatedAt = LocalDate.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDate.now();
    }

    // --- Getters and Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NGO getNgo() {
        return ngo;
    }

    public void setNgo(NGO ngo) {
        this.ngo = ngo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public List<PackageItem> getItems() {
        return items;
    }

    public void setItems(List<PackageItem> items) {
        this.items = items;
    }

    public List<PackageImage> getImages() {
        return images;
    }

    public void setImages(List<PackageImage> images) {
        this.images = images;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDate getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDate updatedAt) {
        this.updatedAt = updatedAt;
    }
}
