package com.repository;

import com.entity.NGODocumentData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NGODocumentDataRepo extends JpaRepository<NGODocumentData,Long> {
}
