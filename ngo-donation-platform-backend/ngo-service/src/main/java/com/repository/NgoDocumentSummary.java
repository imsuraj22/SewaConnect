package com.repository;

/**
 * Lightweight projection — avoids loading {@code document_data} LOB/bytea blobs.
 */
public interface NgoDocumentSummary {

    Long getId();

    String getFileName();
}
