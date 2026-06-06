package com.controller;

import com.dto.ClaimRequestDTO;
import com.dto.DocumentDownload;
import com.dto.NGODocumentDto;
import com.dto.NGODto;
import com.dto.PackageDto;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import com.entity.NGO;
import com.entity.NGOStatus;
import com.ngo.security.JwtUserPrincipal;
import com.service.NGOService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import com.entity.Package;

@RestController
@RequestMapping("/api/ngos")
public class NGOController {

    private final NGOService ngoService;

    public NGOController(NGOService ngoService) {
        this.ngoService = ngoService;
    }

    /** Internal stub creation after user-service signup (no profile body). */
    @PostMapping("/register/{userId}")
    public ResponseEntity<NGO> registerNGO(@PathVariable Long userId) {
        NGO saved = ngoService.registerNGO(userId);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    // ✅ Upload documents (multiple allowed)
    @PostMapping("/{ngoId}/documents")
    public ResponseEntity<Set<NGODocumentDto>> uploadDocuments(
            @PathVariable Long ngoId,
            @RequestParam("files") List<MultipartFile> files,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Set<NGODocumentDto> uploadedDocs = ngoService.uploadDocuments(ngoId, principal.getId(), files);
        return ResponseEntity.ok(uploadedDocs);
    }

    // ✅ Update NGO profile (address, description, location, images, etc.)
    @PutMapping("/{ngoId}")
    public ResponseEntity<NGO> updateNGO(
            @PathVariable Long ngoId,
            @RequestBody NGO updatedData,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        NGO updatedNGO = ngoService.updateNGO(ngoId, principal.getId(), updatedData);
        return ResponseEntity.ok(updatedNGO);
    }

    // ✅ Soft Delete NGO (mark as DEACTIVATED)
    @DeleteMapping("/{ngoId}")
    public ResponseEntity<String> deleteNGO(@PathVariable Long ngoId) {
        ngoService.deactivateNGO(ngoId);
        return ResponseEntity.ok("NGO has been deactivated successfully.");
    }


    @GetMapping("/by-user/{userId}")
    public ResponseEntity<NGODto> getNgoByUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null || !principal.getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ngoService.getOrCreateNGOByUserId(userId));
    }

    /** Current user's NGO workspace (creates stub if missing). */
    @GetMapping("/workspace/me")
    public ResponseEntity<NGODto> myWorkspace(
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ngoService.getOrCreateNGOByUserId(principal.getId()));
    }

    @PostMapping("/{ngoId}/submit-for-review")
    public ResponseEntity<NGODto> submitForReview(
            @PathVariable Long ngoId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(ngoService.submitForReview(ngoId, principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NGODto> getNGO(@PathVariable Long id) {
        return ResponseEntity.ok(ngoService.getNGOById(id));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<NGODto>> getNGO(@PathVariable NGOStatus status) {
        return ResponseEntity.ok(ngoService.getNGOByStatus(status));
    }

    @PostMapping("/{ngoId}/admin/status")
    public ResponseEntity<NGODto> adminSetStatus(
            @PathVariable Long ngoId,
            @RequestParam NGOStatus status,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null || principal.getAuthorities().stream()
                .noneMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(ngoService.updateStatusByAdmin(ngoId, status));
    }

    @GetMapping("/{ngoId}/documents/{documentId}/download")
    public ResponseEntity<byte[]> downloadDocument(
            @PathVariable Long ngoId,
            @PathVariable Long documentId,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        DocumentDownload file = ngoService.getDocumentDownload(ngoId, documentId, principal);
        String fileName = file.getFileName() != null ? file.getFileName() : "document";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file.getData());
    }

    @PostMapping(value = "/{ngoId}/packages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PackageDto> createPackageMultipart(
            @PathVariable Long ngoId,
            @RequestParam String title,
            @RequestParam Double amount,
            @RequestParam(required = false) String items,
            @RequestParam(value = "images", required = false) List<MultipartFile> images,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<String> itemNames = parseItemNames(items);
        PackageDto created = ngoService.createPackage(
                ngoId, principal.getId(), title, amount, itemNames, images);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping(value = "/{ngoId}/packages", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PackageDto> createPackageJson(
            @PathVariable Long ngoId,
            @RequestBody Package pkg,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ngoService.createPackage(ngoId, principal.getId(), pkg));
    }

    @GetMapping("/{ngoId}/packages")
    public ResponseEntity<List<PackageDto>> getPackages(@PathVariable Long ngoId) {
        return ResponseEntity.ok(ngoService.listPackageDtos(ngoId));
    }

    @PostMapping("/{ngoId}/logo")
    public ResponseEntity<Void> uploadLogo(
            @PathVariable Long ngoId,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) throws IOException {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ngoService.uploadOrganizationLogo(ngoId, principal.getId(), file);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{ngoId}/logo")
    public ResponseEntity<byte[]> getLogo(@PathVariable Long ngoId) {
        byte[] logo = ngoService.getOrganizationLogo(ngoId);
        if (logo == null || logo.length == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(logo);
    }

    @GetMapping("/{ngoId}/packages/{packageId}/images/{imageId}")
    public ResponseEntity<byte[]> getPackageImage(
            @PathVariable Long ngoId,
            @PathVariable Long packageId,
            @PathVariable Long imageId
    ) {
        byte[] data = ngoService.getPackageImage(ngoId, packageId, imageId);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(data);
    }

    private static List<String> parseItemNames(String items) {
        if (!StringUtils.hasText(items)) {
            return List.of();
        }
        return Arrays.stream(items.split(","))
                .map(String::trim)
                .filter(StringUtils::hasText)
                .toList();
    }

    @PostMapping("/claim-request/")
    public ResponseEntity<String> createClaimRequest(
            ClaimRequestDTO claimRequestDTO,
            @AuthenticationPrincipal JwtUserPrincipal principal
    ) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ngoService.createClaim(principal.getId(), claimRequestDTO);
        return ResponseEntity.ok("Claim created");
    }



}
