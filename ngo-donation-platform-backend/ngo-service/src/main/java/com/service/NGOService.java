package com.service;

import com.dto.ClaimRequestDTO;
import com.dto.NGODocumentDto;
import com.dto.NGODto;
import com.dto.PackageDto;
import com.entity.NGO;
import com.entity.NGODocument;
import com.entity.NGOStatus;
import com.entity.Package;
import com.entity.PackageImage;
import com.entity.PackageItem;
import com.repository.NGORepository;
import com.repository.NGODocumentRepository;
import com.repository.PackageImageRepository;
import com.repository.PackageRepository;
import com.exceptions.EntityNotFoundException;
import com.dto.DocumentDownload;
import com.dto.NgoRegisterRequest;
import com.ngo.security.JwtUserPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import com.repository.NgoDocumentSummary;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class NGOService {

    private final NGORepository ngoRepository;
    private final NGODocumentRepository documentRepository;
    private final PackageRepository packageRepository;
    private final PackageImageRepository packageImageRepository;
    private final KafkaTemplate<String,NGODto> ngoKafkaTemplate;
    private final KafkaTemplate<String, ClaimRequestDTO> claimRequestKafkaTemplate;

    public NGOService(
            NGORepository ngoRepository,
            NGODocumentRepository documentRepository,
            PackageRepository packageRepository,
            PackageImageRepository packageImageRepository,
            KafkaTemplate<String, NGODto> ngoKafkaTemplate,
            KafkaTemplate<String, ClaimRequestDTO> claimRequestKafkaTemplate
    ) {
        this.ngoRepository = ngoRepository;
        this.documentRepository = documentRepository;
        this.packageRepository = packageRepository;
        this.packageImageRepository = packageImageRepository;
        this.ngoKafkaTemplate = ngoKafkaTemplate;
        this.claimRequestKafkaTemplate = claimRequestKafkaTemplate;
    }

    public NGO registerNGO(Long userId) {
        return registerNGO(userId, null);
    }

    public NGO registerNGO(Long userId, NgoRegisterRequest profile) {
        NGO ngo = new NGO();
        ngo.setUserId(userId);
        ngo.setStatus(NGOStatus.PENDING);
        if (profile != null) {
            ngo.setName(NGO.emptyToNull(profile.getName()));
            ngo.setAddress(NGO.emptyToNull(profile.getAddress()));
            ngo.setDescription(NGO.emptyToNull(profile.getDescription()));
            ngo.setContactEmail(NGO.emptyToNull(profile.getContactEmail()));
        }
        return ngoRepository.save(ngo);
    }

    private NGO requireOwnedNgo(Long ngoId, Long userId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        NgoAccessControl.assertOwner(ngo, userId);
        return ngo;
    }

    public NGODto getNGOByUserId(Long userId) {
        NGO ngo = ngoRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found for user " + userId));
        return toDto(ngo);
    }

    /** Returns existing NGO profile or creates an empty PENDING stub (e.g. after signup). */
    public NGODto getOrCreateNGOByUserId(Long userId) {
        NGO ngo = ngoRepository.findByUserId(userId)
                .orElseGet(() -> registerNGO(userId));
        return toDto(ngo);
    }

    public NGODto updateStatusByAdmin(Long ngoId, NGOStatus status) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        ngo.setStatus(status);
        return toDto(ngoRepository.save(ngo));
    }

    public DocumentDownload getDocumentDownload(Long ngoId, Long documentId, JwtUserPrincipal principal) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        assertAdminOrOwner(principal, ngo);
        NGODocument doc = documentRepository.findByIdAndNgo_Id(documentId, ngoId)
                .orElseThrow(() -> new EntityNotFoundException("Document not found"));
        return new DocumentDownload(doc.getFileName(), doc.getDocumentData());
    }

    private static void assertAdminOrOwner(JwtUserPrincipal principal, NGO ngo) {
        if (principal == null) {
            throw new IllegalStateException("Authentication required");
        }
        boolean admin = principal.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch("ROLE_ADMIN"::equals);
        if (!admin && !principal.getId().equals(ngo.getUserId())) {
            throw new IllegalStateException("Access denied");
        }
    }

    public NGODto submitForReview(Long ngoId, Long userId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        if (!userId.equals(ngo.getUserId())) {
            throw new IllegalStateException("You can only submit your own organization profile");
        }
        if (ngo.getStatus() == NGOStatus.APPROVED) {
            return toDto(ngo);
        }
        long docCount = documentRepository.countByNgo_Id(ngoId);
        List<String> missing = NgoProfileCompletion.missingFields(ngo, docCount);
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Complete your profile before submitting for review: " + String.join(", ", missing));
        }
        ngo.setStatus(NGOStatus.UNDER_REVIEW);
        return toDto(ngoRepository.save(ngo));
    }


    // ✅ Upload one or more documents
    public Set<NGODocumentDto> uploadDocuments(Long ngoId, Long userId, List<MultipartFile> files) throws IOException {
        NGO ngo = requireOwnedNgo(ngoId, userId);
        NgoAccessControl.assertProfileEditable(ngo);

        Set<NGODocumentDto> uploadedDocs = new HashSet<>();

        for (MultipartFile file : files) {
            NGODocument doc = new NGODocument();
            doc.setFileName(file.getOriginalFilename());
            doc.setDocumentData(file.getBytes());
            doc.setNgo(ngo);

            uploadedDocs.add(toDocumentDto(documentRepository.save(doc)));
        }

        return uploadedDocs;
    }

    private NGODocumentDto toDocumentDto(NGODocument doc) {
        NGODocumentDto dto = new NGODocumentDto();
        dto.setId(doc.getId());
        dto.setFileName(doc.getFileName());
        return dto;
    }

    // ✅ Get all docs for NGO
    public Set<NGODocument> getDocuments(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        return ngo.getDocuments();
    }

    // ✅ Update NGO details (only allowed fields)
    // ✅ NGOService
    public NGO updateNGO(Long ngoId, Long userId, NGO updatedData) {
        NGO existing = requireOwnedNgo(ngoId, userId);
        NgoAccessControl.assertProfileEditable(existing);

        if (updatedData.getName() != null) {
            existing.setName(NGO.emptyToNull(updatedData.getName()));
        }
        if (updatedData.getDescription() != null) {
            existing.setDescription(NGO.emptyToNull(updatedData.getDescription()));
        }
        if (updatedData.getAddress() != null) {
            existing.setAddress(NGO.emptyToNull(updatedData.getAddress()));
        }
        if (updatedData.getPhoneNumber() != null) {
            existing.setPhoneNumber(NGO.emptyToNull(updatedData.getPhoneNumber()));
        }
        if (updatedData.getContactEmail() != null) {
            existing.setContactEmail(NGO.emptyToNull(updatedData.getContactEmail()));
        }
        if (updatedData.getImages() != null && !updatedData.getImages().isEmpty()) {
            existing.setImages(updatedData.getImages());
        }

        return ngoRepository.save(existing);
    }


    //admin methods
    // ✅ Get NGO by ID
    public NGODto getNGOById(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        return toDto(ngo);
    }

    private NGODto toDto(NGO ngo) {
        long docCount = documentRepository.countByNgo_Id(ngo.getId());
        NGODto ngoD = new NGODto();
        ngoD.setId(ngo.getId());
        ngoD.setUserId(ngo.getUserId());
        ngoD.setName(ngo.getName());
        ngoD.setAddress(ngo.getAddress());
        ngoD.setDescription(ngo.getDescription());
        ngoD.setPhoneNumber(ngo.getPhoneNumber());
        ngoD.setContactEmail(ngo.getContactEmail());
        ngoD.setNgoStatus(ngo.getStatus().toString());

        List<String> missing = NgoProfileCompletion.missingFields(ngo, docCount);
        ngoD.setMissingProfileFields(missing);
        ngoD.setProfileComplete(missing.isEmpty());
        ngoD.setProfileCompletionPercent(NgoProfileCompletion.percentComplete(ngo, docCount));

        Set<NGODocumentDto> tdocs = new HashSet<>();
        for (NgoDocumentSummary summary : documentRepository.findSummariesByNgo_Id(ngo.getId())) {
            NGODocumentDto nd = new NGODocumentDto();
            nd.setId(summary.getId());
            nd.setFileName(summary.getFileName());
            tdocs.add(nd);
        }
        ngoD.setDocuments(tdocs);
        ngoD.setHasOrganizationImage(ngo.getImages() != null && !ngo.getImages().isEmpty());
        return ngoD;
    }

    public void sendToVerify(NGODto ngoDto){
        NGO ngo=new NGO();
        if(ngoDto.getAddress()!=null) ngo.setAddress(ngoDto.getAddress());
        if(ngoDto.getUserId()!=null) ngo.setUserId(ngoDto.getUserId());
        if(ngoDto.getDescription()!=null) ngo.setDescription(ngoDto.getDescription());
        if(ngoDto.getImages()!=null) ngo.setImages(ngoDto.getImages());
        if(ngoDto.getPhoneNumber()!=null) ngo.setPhoneNumber(ngoDto.getPhoneNumber());
        if(ngoDto.getContactEmail()!=null) ngo.setContactEmail(ngoDto.getContactEmail());
        if(ngoDto.getDocuments()!=null){
            Set<NGODocument> newDocs=new HashSet<>();
            Set<NGODocumentDto> docs=ngoDto.getDocuments();
            for(NGODocumentDto nd:docs){
                NGODocument doc=new NGODocument();
                doc.setNgo(ngo);
                doc.setFileName(nd.getFileName());
                doc.setDocumentData(nd.getDocumentData());
                newDocs.add(doc);
            }
            ngo.setDocuments(newDocs);
        }
        ngoRepository.save(ngo);
        ngoKafkaTemplate.send("ngo-save-request",ngoDto);

    }

    public void deleteNGO(Long id){
        Optional<NGO> user=ngoRepository.findById(id);
        if(user.isPresent()){
            NGO ngo=user.get();
            NGODto ngoD=new NGODto();
            ngoD.setId(ngo.getId());
            ngoD.setUserId(ngo.getUserId());
            ngoD.setName(ngo.getName());
            ngoD.setAddress(ngo.getAddress());
            ngoD.setDescription(ngo.getDescription());
            ngoD.setNgoStatus(ngo.getStatus().toString());
            Set<NGODocument> documents=ngo.getDocuments();
            Set<NGODocumentDto> tdocs=new HashSet<>();
            for(NGODocument ngoDocs:documents){
                NGODocumentDto nd=new NGODocumentDto();
                nd.setId(ngoDocs.getId());
                nd.setFileName(ngoDocs.getFileName());
                nd.setDocumentData(ngoDocs.getDocumentData());
                tdocs.add(nd);
            }
            ngoD.setDocuments(tdocs);
            ngoD.setImages(ngo.getImages());
            ngoD.setPhoneNumber(ngo.getPhoneNumber());
            ngoD.setContactEmail(ngo.getContactEmail());

            ngoKafkaTemplate.send("ngo-save-request",ngoD);
        }

    }

    public void deleteById(Long id){
        ngoRepository.deleteById(id);
    }

    public List<NGODto> getNGOByStatus(NGOStatus status) {
        // Fetch all NGOs with the given status
        List<NGO> ngos = ngoRepository.findByStatus(status);

        // Map each NGO entity to NGODto
        List<NGODto> ngoDtos = new ArrayList<>();
        for (NGO ngo : ngos) {
            ngoDtos.add(toDto(ngo));
        }

        return ngoDtos;
    }


    // ✅ Get all APPROVED NGOs (only visible ones to donors)
    public List<NGO> getAllNGOs() {
        return ngoRepository.findAll()
                .stream()
                .filter(ngo -> ngo.getStatus() == NGOStatus.APPROVED)
                .toList();
    }


    public void deactivateNGO(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));

        ngo.setStatus(NGOStatus.DEACTIVATED); // ✅ soft delete by status
        ngoRepository.save(ngo);
    }


    public void uploadOrganizationLogo(Long ngoId, Long userId, MultipartFile file) throws IOException {
        NGO ngo = requireOwnedNgo(ngoId, userId);
        NgoAccessControl.assertApproved(ngo);
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Logo file is required");
        }
        HashSet<byte[]> logos = new HashSet<>();
        logos.add(file.getBytes());
        ngo.setImages(logos);
        ngoRepository.save(ngo);
    }

    public byte[] getOrganizationLogo(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        if (ngo.getStatus() != NGOStatus.APPROVED) {
            throw new IllegalStateException("Organization is not visible to donors");
        }
        if (ngo.getImages() == null || ngo.getImages().isEmpty()) {
            return null;
        }
        return ngo.getImages().iterator().next();
    }

    public PackageDto createPackage(
            Long ngoId,
            Long userId,
            String title,
            Double amount,
            List<String> itemNames,
            List<MultipartFile> imageFiles
    ) throws IOException {
        NGO ngo = requireOwnedNgo(ngoId, userId);
        NgoAccessControl.assertApproved(ngo);

        Package pkg = new Package();
        pkg.setNgo(ngo);
        pkg.setTitle(title.trim());
        pkg.setAmount(amount);

        if (itemNames != null) {
            List<PackageItem> items = new ArrayList<>();
            for (String name : itemNames) {
                if (StringUtils.hasText(name)) {
                    PackageItem item = new PackageItem();
                    item.setName(name.trim());
                    items.add(item);
                }
            }
            pkg.setItems(items);
        }

        Package saved = packageRepository.save(pkg);

        if (imageFiles != null) {
            for (MultipartFile file : imageFiles) {
                if (file == null || file.isEmpty()) {
                    continue;
                }
                PackageImage img = new PackageImage();
                img.setImage(file.getBytes());
                img.setPkg(saved);
                packageImageRepository.save(img);
            }
        }

        return toPackageDto(packageRepository.findById(saved.getId()).orElse(saved));
    }

    public PackageDto createPackage(Long ngoId, Long userId, Package pkg) {
        List<String> itemNames = new ArrayList<>();
        if (pkg.getItems() != null) {
            for (PackageItem item : pkg.getItems()) {
                if (item != null && StringUtils.hasText(item.getName())) {
                    itemNames.add(item.getName());
                }
            }
        }
        try {
            return createPackage(ngoId, userId, pkg.getTitle(), pkg.getAmount(), itemNames, List.of());
        } catch (IOException e) {
            throw new IllegalStateException("Could not create package", e);
        }
    }

    public List<PackageDto> listPackageDtos(Long ngoId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        if (ngo.getStatus() != NGOStatus.APPROVED) {
            throw new IllegalStateException("Organization is not visible to donors");
        }
        return packageRepository.findByNgoId(ngoId).stream().map(this::toPackageDto).toList();
    }

    public byte[] getPackageImage(Long ngoId, Long packageId, Long imageId) {
        NGO ngo = ngoRepository.findById(ngoId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found with id " + ngoId));
        if (ngo.getStatus() != NGOStatus.APPROVED) {
            throw new IllegalStateException("Organization is not visible to donors");
        }
        PackageImage image = packageImageRepository.findByIdAndPkg_Id(imageId, packageId)
                .orElseThrow(() -> new EntityNotFoundException("Package image not found"));
        return image.getImage();
    }

    private PackageDto toPackageDto(Package pkg) {
        PackageDto dto = new PackageDto();
        dto.setId(pkg.getId());
        dto.setTitle(pkg.getTitle());
        dto.setAmount(pkg.getAmount());
        dto.setItems(pkg.getItems() != null ? pkg.getItems() : List.of());
        dto.setImageIds(packageImageRepository.findIdsByPackageId(pkg.getId()));
        return dto;
    }
    public void createClaim(Long userId, ClaimRequestDTO claimRequestDTO) {
        NGO ngo = ngoRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("NGO not found for user " + userId));
        NgoAccessControl.assertApproved(ngo);
        claimRequestKafkaTemplate.send("claim-create-request", claimRequestDTO);
    }


}
