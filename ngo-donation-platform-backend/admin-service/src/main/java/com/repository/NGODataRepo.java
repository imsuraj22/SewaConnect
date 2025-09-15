package com.repository;

import com.entity.NGOData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NGODataRepo extends JpaRepository<NGOData,Long> {
}
