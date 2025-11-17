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
}
