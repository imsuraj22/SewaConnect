package com.dto;

import com.entity.PackageItem;

import java.util.ArrayList;
import java.util.List;

public class PackageDto {

    private Long id;
    private String title;
    private Double amount;
    private List<PackageItem> items = new ArrayList<>();
    private List<Long> imageIds = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
        this.items = items != null ? items : new ArrayList<>();
    }

    public List<Long> getImageIds() {
        return imageIds;
    }

    public void setImageIds(List<Long> imageIds) {
        this.imageIds = imageIds != null ? imageIds : new ArrayList<>();
    }
}
