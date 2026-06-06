package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.HashSet;
import java.util.Set;

@Entity
public class UserData {

    @Id
    private Long id;
    private String username;
    private String email;
    private Set<String> roles = new HashSet<>();

    public UserData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }
}
