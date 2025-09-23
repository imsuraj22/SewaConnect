package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class NGODocumentData {
    @Id
    private Long id;
    private String fileName;   // e.g. "REGISTRATION_CERTIFICATE"
    private byte[] documentData;
    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private NGOData ngo;
}
