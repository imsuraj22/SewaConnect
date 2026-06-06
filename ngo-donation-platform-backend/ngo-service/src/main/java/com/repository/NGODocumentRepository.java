package com.repository;

import com.entity.NGODocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NGODocumentRepository extends JpaRepository<NGODocument, Long> {

    long countByNgo_Id(Long ngoId);

    Optional<NGODocument> findByIdAndNgo_Id(Long id, Long ngoId);

    @Query("SELECT d.id AS id, d.fileName AS fileName FROM NGODocument d WHERE d.ngo.id = :ngoId")
    List<NgoDocumentSummary> findSummariesByNgo_Id(@Param("ngoId") Long ngoId);
}
