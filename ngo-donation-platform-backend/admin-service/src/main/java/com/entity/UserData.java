package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class UserData {
    @Id
    private Long id;
    private String username;
    private String email;
    private Set<String> roles = new HashSet<>();
}
