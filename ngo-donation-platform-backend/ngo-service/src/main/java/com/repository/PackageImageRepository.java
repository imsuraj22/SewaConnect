package com.repository;

import com.entity.PackageImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PackageImageRepository extends JpaRepository<PackageImage, Long> {

    @Query("SELECT pi.id FROM PackageImage pi WHERE pi.pkg.id = :packageId")
    List<Long> findIdsByPackageId(@Param("packageId") Long packageId);

    Optional<PackageImage> findByIdAndPkg_Id(Long id, Long packageId);
}
