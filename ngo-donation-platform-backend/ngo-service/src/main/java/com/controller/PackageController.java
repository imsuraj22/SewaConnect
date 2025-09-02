package com.controller;

import com.entity.Package;
import com.entity.PackageImage;
import com.entity.PackageItem;
import com.service.PackageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/packages")
public class PackageController {

    private final PackageService packageService;

    public PackageController(PackageService packageService) {
        this.packageService = packageService;
    }

    // Get single package
    @GetMapping("/{packageId}")
    public ResponseEntity<Package> getPackage(@PathVariable Long packageId) {
        return ResponseEntity.ok(packageService.getPackageById(packageId));
    }

    // Update package (title, amount, etc.)
    @PutMapping("/{packageId}")
    public ResponseEntity<Package> updatePackage(
            @PathVariable Long packageId,
            @RequestBody Package pkg) {
        return ResponseEntity.ok(packageService.updatePackageById(packageId, pkg));
    }

    // Delete package
    @DeleteMapping("/{packageId}")
    public ResponseEntity<Void> deletePackage(@PathVariable Long packageId) {
        packageService.deletePackageById(packageId);
        return ResponseEntity.noContent().build();
    }

    // Add item
    @PostMapping("/{packageId}/items")
    public ResponseEntity<Package> addItem(
            @PathVariable Long packageId,
            @RequestBody PackageItem item) {
        return ResponseEntity.ok(packageService.addItemToPackage(packageId, item));
    }

    // Remove item
    @DeleteMapping("/{packageId}/items/{itemId}")
    public ResponseEntity<Package> removeItem(
            @PathVariable Long packageId,
            @PathVariable String itemName) {
        return ResponseEntity.ok(packageService.removeItemFromPackage(packageId, itemName));
    }

    // Add image
    @PostMapping("/{packageId}/images")
    public ResponseEntity<Package> addImage(
            @PathVariable Long packageId,
            @RequestBody PackageImage image) {
        return ResponseEntity.ok(packageService.addImageToPackage(packageId, image));
    }

    // Remove image
    @DeleteMapping("/{packageId}/images/{imageId}")
    public ResponseEntity<Package> removeImage(
            @PathVariable Long packageId,
            @PathVariable Long imageId) {
        return ResponseEntity.ok(packageService.removeImageFromPackage(packageId, imageId));
    }
}
