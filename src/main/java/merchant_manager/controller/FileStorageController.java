package merchant_manager.controller;

import merchant_manager.dto.FileUploadResponse;
import merchant_manager.models.FileMetadata;
import merchant_manager.service.implementation.CloudStorageServiceImp;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
public class FileStorageController {

    private final CloudStorageServiceImp cloudStorageService;

    public FileStorageController(CloudStorageServiceImp cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

    /**
     * Upload a file
     * Request params:
     * - file: the file to upload (multipart/form-data)
     * - entityType: optional entity type (e.g., "merchant", "user")
     * - entityId: optional entity ID
     * - isPublic: optional boolean for public access
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FileUploadResponse> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {

        FileUploadResponse response = cloudStorageService.uploadFile(file, entityType, entityId, isPublic);
        return ResponseEntity.ok(response);
    }

    /**
     * Upload multiple files
     */
    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "entityType", required = false) String entityType,
            @RequestParam(value = "entityId", required = false) Long entityId,
            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {

        Map<String, Object> response = new HashMap<>();
        try {
            List<FileUploadResponse> uploadedFiles = new java.util.ArrayList<>();
            for (MultipartFile file : files) {
                FileUploadResponse fileResponse = cloudStorageService.uploadFile(file, entityType, entityId, isPublic);
                uploadedFiles.add(fileResponse);
            }
            response.put("status", "success");
            response.put("files", uploadedFiles);
            response.put("count", uploadedFiles.size());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Download a file by ID
     */
    @GetMapping("/download/{fileId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = cloudStorageService.getFileMetadata(fileId);
            InputStream fileStream = cloudStorageService.downloadFile(fileId);

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + metadata.getOriginalFilename() + "\"");
            headers.setContentType(MediaType.parseMediaType(metadata.getContentType()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(metadata.getFileSize())
                    .body(new InputStreamResource(fileStream));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get file metadata by ID
     */
    @GetMapping("/metadata/{fileId}")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = cloudStorageService.getFileMetadata(fileId);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get file URL by ID
     */
    @GetMapping("/url/{fileId}")
    public ResponseEntity<Map<String, String>> getFileUrl(@PathVariable Long fileId) {
        try {
            String url = cloudStorageService.getFileUrl(fileId);
            Map<String, String> response = new HashMap<>();
            response.put("fileId", fileId.toString());
            response.put("url", url);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get all files uploaded by a user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<FileMetadata>> getFilesByUser(@PathVariable Long userId) {
        List<FileMetadata> files = cloudStorageService.getFilesByUser(userId);
        return ResponseEntity.ok(files);
    }

    /**
     * Get all files for an entity
     */
    @GetMapping("/entity/{entityType}/{entityId}")
    public ResponseEntity<List<FileMetadata>> getFilesByEntity(
            @PathVariable String entityType,
            @PathVariable Long entityId) {
        List<FileMetadata> files = cloudStorageService.getFilesByEntity(entityType, entityId);
        return ResponseEntity.ok(files);
    }

    /**
     * Delete a file
     */
    @DeleteMapping("/{fileId}")
    public ResponseEntity<Map<String, String>> deleteFile(@PathVariable Long fileId) {
        try {
            cloudStorageService.deleteFile(fileId);
            Map<String, String> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "File deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
