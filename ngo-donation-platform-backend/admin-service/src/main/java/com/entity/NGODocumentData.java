package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class NGODocumentData {
    private Long id;
    private String fileName;   // e.g. "REGISTRATION_CERTIFICATE"
    private byte[] documentData;
    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private NGOData ngo;
}
