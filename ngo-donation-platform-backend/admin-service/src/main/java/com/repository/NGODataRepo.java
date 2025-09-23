package com.repository;

import com.entity.NGOData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NGODataRepo extends JpaRepository<NGOData,Long> {
}
