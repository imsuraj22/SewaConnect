package com.dto;

import com.entity.NGODocument;
import com.entity.NGOStatus;
import lombok.Data;
import java.util.List;
import java.util.Set;

@Data
public class NGODto {
    private Long id;
    private Long userId;
    private String name;
    private String address;
    private String description;
    private String ngoStatus;
    private Set<NGODocumentDto> documents;
    private Set<byte[]> images;  // return Base64 images
    private Double locationLat;
    private Double locationLng;
}
