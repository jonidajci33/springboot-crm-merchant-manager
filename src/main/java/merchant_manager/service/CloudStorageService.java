package merchant_manager.service;

import merchant_manager.dto.FileUploadResponse;
import merchant_manager.models.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface CloudStorageService {

    /**
     * Upload a file to cloud storage
     *
     * @param file the file to upload
     * @param entityType optional entity type (e.g., "merchant", "user")
     * @param entityId optional entity ID
     * @param isPublic whether the file should be publicly accessible
     * @return FileUploadResponse with file details
     */
    FileUploadResponse uploadFile(MultipartFile file, String entityType, Long entityId, Boolean isPublic);

    /**
     * Download a file from cloud storage
     *
     * @param fileId the file metadata ID
     * @return InputStream of the file
     */
    InputStream downloadFile(Long fileId);

    /**
     * Delete a file from cloud storage
     *
     * @param fileId the file metadata ID
     */
    void deleteFile(Long fileId);

    /**
     * Get file metadata by ID
     *
     * @param fileId the file metadata ID
     * @return FileMetadata
     */
    FileMetadata getFileMetadata(Long fileId);

    /**
     * Get all files uploaded by a user
     *
     * @param userId the user ID
     * @return List of FileMetadata
     */
    List<FileMetadata> getFilesByUser(Long userId);

    /**
     * Get all files associated with an entity
     *
     * @param entityType entity type (e.g., "merchant", "user")
     * @param entityId entity ID
     * @return List of FileMetadata
     */
    List<FileMetadata> getFilesByEntity(String entityType, Long entityId);

    /**
     * Get public URL for a file
     *
     * @param fileId the file metadata ID
     * @return public URL string
     */
    String getFileUrl(Long fileId);
}
