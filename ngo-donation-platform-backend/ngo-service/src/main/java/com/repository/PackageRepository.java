package com.repository;

import com.entity.Package;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageRepository extends JpaRepository<Package,Long> {
    List<Package> findByNgoId(Long ngoId);

}
