package com.entity;

import com.dto.NGODocumentDto;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class NGOData {
    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String description;
    private String ngoStatus;
    @OneToMany(mappedBy = "ngo", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NGODocumentData> documents = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "ngo_images", joinColumns = @JoinColumn(name = "ngo_id"))
    @Column(name = "image", columnDefinition = "LONGBLOB")
    private Set<byte[]> images = new HashSet<>();
    private Double locationLat;
    private Double locationLng;
}
