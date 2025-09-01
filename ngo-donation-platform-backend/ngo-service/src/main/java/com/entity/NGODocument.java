package com.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class NGODocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Link to NGO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ngo_id", nullable = false)
    private NGO ngo;

    public Long getId() {
        return id;
    }

    public NGO getNgo() {
        return ngo;
    }
    @Column(nullable = false)
    private String fileName;
    // e.g. "REGISTRATION_CERTIFICATE", "PAN_CARD", "TRUST_DEED"

    // Store actual file
    @Lob
    @Column(name = "document_data", columnDefinition = "LONGBLOB")
    private byte[] documentData;

    private LocalDateTime uploadedAt;

    public void setNgo(NGO ngo) {
        this.ngo = ngo;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getDocumentData() {
        return documentData;
    }

    public void setDocumentData(byte[] documentData) {
        this.documentData = documentData;
    }

    // Type of document


    @PrePersist
    public void onUpload() {
        this.uploadedAt = LocalDateTime.now();
    }


}

