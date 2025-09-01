package com.repository;

import com.entity.NGODocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NGODocumentRepository extends JpaRepository<NGODocument, Long> {
}