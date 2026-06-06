package com.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class NGODocumentData {

    @Id
    private Long id;
    private String fileName;
    private byte[] documentData;

    @ManyToOne
    @JoinColumn(name = "ngo_id")
    private NGOData ngo;

    public NGODocumentData() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public NGOData getNgo() {
        return ngo;
    }

    public void setNgo(NGOData ngo) {
        this.ngo = ngo;
    }
}
