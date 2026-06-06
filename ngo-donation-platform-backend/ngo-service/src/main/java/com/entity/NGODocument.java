package com.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name = "ngo_document")
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

    public void setId(Long id) {
        this.id = id;
    }

    @JsonIgnore
    public NGO getNgo() {
        return ngo;
    }
    @Column(nullable = false)
    private String fileName;
    // e.g. "REGISTRATION_CERTIFICATE", "PAN_CARD", "TRUST_DEED"

    /** PostgreSQL {@code bytea} — avoids OID/large-object auto-commit errors. */
    @JdbcTypeCode(SqlTypes.VARBINARY)
    @Column(name = "document_data", columnDefinition = "bytea")
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

    @JsonIgnore
    public byte[] getDocumentData() {
        return documentData;
    }

    public void setDocumentData(byte[] documentData) {
        this.documentData = documentData;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }

    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }

    @PrePersist
    public void onUpload() {
        this.uploadedAt = LocalDateTime.now();
    }


}

