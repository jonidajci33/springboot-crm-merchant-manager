package merchant_manager.service;

import merchant_manager.dto.FileUploadResponse;
import merchant_manager.models.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

public interface CloudStorageService {


    FileMetadata uploadFile(MultipartFile file);

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
//    List<FileMetadata> getFilesByEntity(String entityType, Long entityId);

    /**
     * Get public URL for a file
     *
     * @param fileId the file metadata ID
     * @return public URL string
     */
    String getFileUrl(Long fileId);

    /**
     * Create a signed URL for a file with 1 hour expiration
     * If a valid cached URL exists (less than 1 hour old), returns the cached URL
     * Otherwise, creates a new signed URL and updates the cache
     *
     * @param fileId the file metadata ID
     * @return signed URL string with 1 hour expiration
     */
    String createSignedUrl(Long fileId);
}
