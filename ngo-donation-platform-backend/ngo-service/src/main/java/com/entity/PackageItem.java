package com.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PackageItem {

    private String name;

    // --- Getters & Setters ---
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
