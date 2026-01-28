package merchant_manager.service.implementation;

import merchant_manager.config.CloudStorageProperties;
import merchant_manager.dto.FileUploadResponse;
import merchant_manager.models.FileMetadata;
import merchant_manager.models.SignedUrlCache;
import merchant_manager.models.User;
import merchant_manager.repository.FileMetadataRepository;
import merchant_manager.repository.SignedUrlCacheRepository;
import merchant_manager.service.CloudStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Service
public class CloudStorageServiceImp implements CloudStorageService {

    private static final Logger logger = LoggerFactory.getLogger(CloudStorageServiceImp.class);

    private final FileMetadataRepository fileMetadataRepository;
    private final SignedUrlCacheRepository signedUrlCacheRepository;
    private final CloudStorageProperties storageProperties;
    private final SupabaseStorageService supabaseStorageService;

    public CloudStorageServiceImp(FileMetadataRepository fileMetadataRepository,
                                  SignedUrlCacheRepository signedUrlCacheRepository,
                                  CloudStorageProperties storageProperties,
                                  SupabaseStorageService supabaseStorageService) {
        this.fileMetadataRepository = fileMetadataRepository;
        this.signedUrlCacheRepository = signedUrlCacheRepository;
        this.storageProperties = storageProperties;
        this.supabaseStorageService = supabaseStorageService;
        initializeStorage();
    }

    private void initializeStorage() {
        if ("local".equalsIgnoreCase(storageProperties.getProvider())) {
            try {
                String storagePath = storageProperties.getLocalStoragePath();
                if (storagePath == null || storagePath.isEmpty()) {
                    storagePath = "uploads";
                    storageProperties.setLocalStoragePath(storagePath);
                }
                Path uploadPath = Paths.get(storagePath);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                    logger.info("Created local storage directory: {}", uploadPath.toAbsolutePath());
                }
            } catch (IOException e) {
                logger.error("Failed to create storage directory", e);
                throw new RuntimeException("Could not initialize storage", e);
            }
        }
    }

    @Override
    public FileMetadata uploadFile(MultipartFile file, Boolean skipUser) {
        // Validate file
        if (file.isEmpty()) {
            throw new RuntimeException("Cannot upload empty file");
        }

        if (file.getSize() > storageProperties.getMaxFileSize()) {
            throw new RuntimeException("File size exceeds maximum allowed size");
        }

        try {
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String storedFilename = generateUniqueFilename(extension);

            String fileUrl;
            String filePath;

            // Upload based on provider
            if ("supabase".equalsIgnoreCase(storageProperties.getProvider())) {
                fileUrl = supabaseStorageService.uploadFile(file, storedFilename);
                filePath = storageProperties.getBucketName() + "/" + storedFilename;
            } else if ("aws".equalsIgnoreCase(storageProperties.getProvider())) {
                fileUrl = uploadToAWS(file, storedFilename);
                filePath = storageProperties.getBucketName() + "/" + storedFilename;
            } else if ("azure".equalsIgnoreCase(storageProperties.getProvider())) {
                fileUrl = uploadToAzure(file, storedFilename);
                filePath = storageProperties.getBucketName() + "/" + storedFilename;
            } else if ("gcp".equalsIgnoreCase(storageProperties.getProvider())) {
                fileUrl = uploadToGCP(file, storedFilename);
                filePath = storageProperties.getBucketName() + "/" + storedFilename;
            } else {
                // Default to local storage
                filePath = uploadToLocal(file, storedFilename);
                fileUrl = "/api/files/download/" + storedFilename;
            }

            // Save metadata to database
            FileMetadata metadata = new FileMetadata();
            metadata.setOriginalFilename(originalFilename);
            metadata.setStoredFilename(storedFilename);
            metadata.setFilePath(filePath);
            metadata.setFileSize(file.getSize());
            metadata.setContentType(file.getContentType());
            metadata.setCloudProvider(storageProperties.getProvider());
            metadata.setBucketName(storageProperties.getBucketName());
            metadata.setFileUrl(fileUrl);
            if(!skipUser) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                User currentUser = authentication != null ? (User) authentication.getPrincipal() : null;
                metadata.setUploadedBy(currentUser);
            }
            return fileMetadataRepository.save(metadata);

        } catch (Exception e) {
            logger.error("Error uploading file", e);
            throw new RuntimeException("Failed to upload file: " + e.getMessage(), e);
        }
    }

    @Override
    public InputStream downloadFile(Long fileId) {
        FileMetadata metadata = getFileMetadata(fileId);

        try {
            if ("supabase".equalsIgnoreCase(metadata.getCloudProvider())) {
                return supabaseStorageService.downloadFile(metadata.getStoredFilename());
            } else if ("aws".equalsIgnoreCase(metadata.getCloudProvider())) {
                return downloadFromAWS(metadata.getStoredFilename());
            } else if ("azure".equalsIgnoreCase(metadata.getCloudProvider())) {
                return downloadFromAzure(metadata.getStoredFilename());
            } else if ("gcp".equalsIgnoreCase(metadata.getCloudProvider())) {
                return downloadFromGCP(metadata.getStoredFilename());
            } else {
                return downloadFromLocal(metadata.getStoredFilename());
            }
        } catch (Exception e) {
            logger.error("Error downloading file: {}", fileId, e);
            throw new RuntimeException("Failed to download file", e);
        }
    }

    @Override
    public void deleteFile(Long fileId) {
        FileMetadata metadata = getFileMetadata(fileId);

        try {
            if ("supabase".equalsIgnoreCase(metadata.getCloudProvider())) {
                supabaseStorageService.deleteFile(metadata.getStoredFilename());
            } else if ("aws".equalsIgnoreCase(metadata.getCloudProvider())) {
                deleteFromAWS(metadata.getStoredFilename());
            } else if ("azure".equalsIgnoreCase(metadata.getCloudProvider())) {
                deleteFromAzure(metadata.getStoredFilename());
            } else if ("gcp".equalsIgnoreCase(metadata.getCloudProvider())) {
                deleteFromGCP(metadata.getStoredFilename());
            } else {
                deleteFromLocal(metadata.getStoredFilename());
            }

            fileMetadataRepository.deleteById(fileId);
            logger.info("File deleted successfully: {}", fileId);

        } catch (Exception e) {
            logger.error("Error deleting file: {}", fileId, e);
            throw new RuntimeException("Failed to delete file", e);
        }
    }

    @Override
    public FileMetadata getFileMetadata(Long fileId) {
        return fileMetadataRepository.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }

    @Override
    public List<FileMetadata> getFilesByUser(Long userId) {
        return fileMetadataRepository.findByUploadedById(userId);
    }

//    @Override
//    public List<FileMetadata> getFilesByEntity(String entityType, Long entityId) {
//        return fileMetadataRepository.findByEntityTypeAndEntityId(entityType, entityId);
//    }

    @Override
    public String getFileUrl(Long fileId) {
        FileMetadata metadata = getFileMetadata(fileId);
        return metadata.getFileUrl();
    }

    @Override
    public String createSignedUrl(Long fileId) {
        FileMetadata metadata = getFileMetadata(fileId);

        // Check if there's a cached signed URL
        var cachedUrlOpt = signedUrlCacheRepository.findByFileMetadataId(fileId);

        if (cachedUrlOpt.isPresent()) {
            SignedUrlCache cachedUrl = cachedUrlOpt.get();

            // If the cached URL is still valid (less than 1 hour old), return it
            if (cachedUrl.isValid()) {
                logger.info("Returning cached signed URL for file: {}", fileId);
                return cachedUrl.getSignedUrl();
            } else {
                logger.info("Cached signed URL expired for file: {}, creating new one", fileId);
            }
        }

        // Create a new signed URL
        String newSignedUrl;

        if ("supabase".equalsIgnoreCase(metadata.getCloudProvider())) {
            try {
                // 1 hour = 3600 seconds
                newSignedUrl = supabaseStorageService.createSignedUrl(metadata.getStoredFilename(), 3600);
            } catch (Exception e) {
                // If signed URL creation fails (e.g., bucket is public), fall back to public URL
                logger.warn("Failed to create signed URL for file {}: {}. Falling back to public URL.",
                        fileId, e.getMessage());

                // Return the public URL instead
                newSignedUrl = supabaseStorageService.getPublicUrl(metadata.getStoredFilename());
            }
        } else {
            // For other providers, you can implement similar logic
            // For now, fallback to the regular file URL
            logger.warn("Signed URL not supported for provider: {}, returning regular URL", metadata.getCloudProvider());
            return metadata.getFileUrl();
        }

        // Save or update the cache
        SignedUrlCache urlCache;
        if (cachedUrlOpt.isPresent()) {
            // Update existing cache
            urlCache = cachedUrlOpt.get();
            urlCache.setSignedUrl(newSignedUrl);
            urlCache.setCreatedAt(LocalDateTime.now());
            urlCache.setExpiresAt(LocalDateTime.now().plusHours(1));
        } else {
            // Create new cache entry
            urlCache = new SignedUrlCache();
            urlCache.setFileMetadata(metadata);
            urlCache.setSignedUrl(newSignedUrl);
            urlCache.setCreatedAt(LocalDateTime.now());
            urlCache.setExpiresAt(LocalDateTime.now().plusHours(1));
        }

        signedUrlCacheRepository.save(urlCache);
        logger.info("Created and cached new signed URL for file: {}", fileId);

        return newSignedUrl;
    }

    // Helper methods

    private String generateUniqueFilename(String extension) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);
        return timestamp + "_" + uuid + extension;
    }

    // Local storage methods

    private String uploadToLocal(MultipartFile file, String filename) throws IOException {
        Path uploadPath = Paths.get(storageProperties.getLocalStoragePath());
        Path filePath = uploadPath.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filePath.toString();
    }

    private InputStream downloadFromLocal(String filename) throws IOException {
        Path filePath = Paths.get(storageProperties.getLocalStoragePath()).resolve(filename);
        return Files.newInputStream(filePath);
    }

    private void deleteFromLocal(String filename) throws IOException {
        Path filePath = Paths.get(storageProperties.getLocalStoragePath()).resolve(filename);
        Files.deleteIfExists(filePath);
    }

    // AWS S3 methods (placeholders - implement with AWS SDK)

    private String uploadToAWS(MultipartFile file, String filename) {
        // TODO: Implement AWS S3 upload using AWS SDK
        // Example: s3Client.putObject(bucketName, filename, file.getInputStream(), metadata)
        throw new UnsupportedOperationException("AWS S3 upload not yet implemented. Please add AWS SDK dependency and implement.");
    }

    private InputStream downloadFromAWS(String filename) {
        // TODO: Implement AWS S3 download
        throw new UnsupportedOperationException("AWS S3 download not yet implemented");
    }

    private void deleteFromAWS(String filename) {
        // TODO: Implement AWS S3 delete
        throw new UnsupportedOperationException("AWS S3 delete not yet implemented");
    }

    // Azure Blob Storage methods (placeholders - implement with Azure SDK)

    private String uploadToAzure(MultipartFile file, String filename) {
        // TODO: Implement Azure Blob Storage upload
        throw new UnsupportedOperationException("Azure Blob Storage upload not yet implemented. Please add Azure SDK dependency and implement.");
    }

    private InputStream downloadFromAzure(String filename) {
        // TODO: Implement Azure download
        throw new UnsupportedOperationException("Azure Blob Storage download not yet implemented");
    }

    private void deleteFromAzure(String filename) {
        // TODO: Implement Azure delete
        throw new UnsupportedOperationException("Azure Blob Storage delete not yet implemented");
    }

    // Google Cloud Storage methods (placeholders - implement with GCP SDK)

    private String uploadToGCP(MultipartFile file, String filename) {
        // TODO: Implement GCP Cloud Storage upload
        throw new UnsupportedOperationException("GCP Cloud Storage upload not yet implemented. Please add GCP SDK dependency and implement.");
    }

    private InputStream downloadFromGCP(String filename) {
        // TODO: Implement GCP download
        throw new UnsupportedOperationException("GCP Cloud Storage download not yet implemented");
    }

    private void deleteFromGCP(String filename) {
        // TODO: Implement GCP delete
        throw new UnsupportedOperationException("GCP Cloud Storage delete not yet implemented");
    }
}
