package com.dto;

import lombok.Data;

@Data
public class NGODocumentDto {
    private Long id;
    private String fileName;   // e.g. "REGISTRATION_CERTIFICATE"
    private byte[] documentData;
}
