package com.repository;

import com.entity.NGODocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NGODocumentRepository extends JpaRepository<NGODocument, Long> {
}