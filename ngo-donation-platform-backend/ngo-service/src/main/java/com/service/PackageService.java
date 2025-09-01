package com.service;

import com.entity.Package;
import com.repository.PackageRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PackageService {

    private final PackageRepository packageRepository;

    public PackageService(PackageRepository packageRepository) {
        this.packageRepository = packageRepository;
    }

    // Create new package
    public Package createPackage(Package pkg) {
        return packageRepository.save(pkg);
    }

    // Update package
    public Package updatePackage(Long id, Package pkgDetails) {
        Package pkg = packageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Package not found with id: " + id));

        pkg.setTitle(pkgDetails.getTitle());
        pkg.setAmount(pkgDetails.getAmount());
        pkg.setItems(pkgDetails.getItems());
        pkg.setImages(pkgDetails.getImages());
        return packageRepository.save(pkg);
    }

    // Soft delete (set inactive if you add "active" field) or full delete
    public void deletePackage(Long id) {
        if (!packageRepository.existsById(id)) {
            throw new EntityNotFoundException("Package not found with id: " + id);
        }
        packageRepository.deleteById(id);
    }

    // Get package by id
    public Package getPackageById(Long id) {
        return packageRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Package not found with id: " + id));
    }

    // Get all packages
    public List<Package> getAllPackages() {
        return packageRepository.findAll();
    }

    // Get packages by NGO
    public List<Package> getPackagesByNgo(Long ngoId) {
        return packageRepository.findByNgoId(ngoId);
    }
}
