package com.service;

import com.entity.Package;
import com.entity.PackageImage;
import com.entity.PackageItem;
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

    public Package getPackageById(Long packageId){
        return packageRepository.findById(packageId).orElse(null);
    }



    public Package updatePackageById(Long packageId, Package pkg) {
        Package existing = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        // update simple fields
        if (pkg.getTitle() != null) existing.setTitle(pkg.getTitle());
        if (pkg.getAmount() != null) existing.setAmount(pkg.getAmount());

        // update items (replace list if provided)
        if (pkg.getItems() != null && !pkg.getItems().isEmpty()) {
            existing.getItems().clear();
            existing.getItems().addAll(pkg.getItems());
        }

        // update images (replace list if provided)
        if (pkg.getImages() != null && !pkg.getImages().isEmpty()) {
            // orphanRemoval = true ensures old ones get deleted
           // existing.getImages().clear();
            for (PackageImage img : pkg.getImages()) {
                img.setPkg(existing); // set FK properly
                existing.getImages().add(img);
            }
        }

        return packageRepository.save(existing); // cascades updates
    }

    public String deletePackageById(Long packageId){
        packageRepository.deleteById(packageId);
        return "Package deleted Successfully";
    }

    // add new image
    public Package addImageToPackage(Long packageId, PackageImage image) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        image.setPkg(pkg);
        pkg.getImages().add(image);

        return packageRepository.save(pkg);
    }

    // remove image
    public Package removeImageFromPackage(Long packageId, Long imageId) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        boolean removed = pkg.getImages().removeIf(img -> img.getId().equals(imageId));
        if (!removed) throw new EntityNotFoundException("Image not found");

        return packageRepository.save(pkg);
    }

    // add new item
    public Package addItemToPackage(Long packageId, PackageItem item) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        pkg.getItems().add(item);
        return packageRepository.save(pkg);
    }

    // remove item (assuming PackageItem has equals/hashCode on fields)
    public Package removeItemFromPackage(Long packageId, String itemName) {
        Package pkg = packageRepository.findById(packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package not found"));

        boolean removed = pkg.getItems().remove(itemName);
        if (!removed) throw new EntityNotFoundException("Item not found");

        return packageRepository.save(pkg);
    }

}
