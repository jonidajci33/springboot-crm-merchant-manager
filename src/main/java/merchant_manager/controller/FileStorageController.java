package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "File Storage", description = "Upload, download, and manage files in cloud storage (Supabase)")
public class FileStorageController {

    private final CloudStorageServiceImp cloudStorageService;

    public FileStorageController(CloudStorageServiceImp cloudStorageService) {
        this.cloudStorageService = cloudStorageService;
    }

//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "Upload file", description = "Upload a single file to cloud storage with optional entity association")
//    public ResponseEntity<FileUploadResponse> uploadFile(
//            @RequestParam("file") MultipartFile file,
//            @RequestParam(value = "entityType", required = false) String entityType,
//            @RequestParam(value = "entityId", required = false) Long entityId,
//            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {
//
//        FileUploadResponse response = cloudStorageService.uploadFile(file, entityType, entityId, isPublic);
//        return ResponseEntity.ok(response);
//    }

//    @PostMapping(value = "/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    @Operation(summary = "Upload multiple files", description = "Upload multiple files to cloud storage in a single request")
//    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(
//            @RequestParam("files") MultipartFile[] files,
//            @RequestParam(value = "entityType", required = false) String entityType,
//            @RequestParam(value = "entityId", required = false) Long entityId,
//            @RequestParam(value = "isPublic", required = false, defaultValue = "false") Boolean isPublic) {
//
//        Map<String, Object> response = new HashMap<>();
//        try {
//            List<FileUploadResponse> uploadedFiles = new java.util.ArrayList<>();
//            for (MultipartFile file : files) {
//                FileUploadResponse fileResponse = cloudStorageService.uploadFile(file, entityType, entityId, isPublic);
//                uploadedFiles.add(fileResponse);
//            }
//            response.put("status", "success");
//            response.put("files", uploadedFiles);
//            response.put("count", uploadedFiles.size());
//            return ResponseEntity.ok(response);
//        } catch (Exception e) {
//            response.put("status", "error");
//            response.put("message", e.getMessage());
//            return ResponseEntity.badRequest().body(response);
//        }
//    }

    @GetMapping("/download/{fileId}")
    @Operation(summary = "Download file", description = "Download a file from cloud storage by file ID")
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

    @GetMapping("/metadata/{fileId}")
    @Operation(summary = "Get file metadata", description = "Retrieve metadata for a specific file")
    public ResponseEntity<FileMetadata> getFileMetadata(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = cloudStorageService.getFileMetadata(fileId);
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/url/{fileId}")
    @Operation(summary = "Get file URL", description = "Get the cloud storage URL for a file")
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

    @GetMapping("/public/signed-url/{fileId}")
    @Operation(
        summary = "Get signed URL",
        description = "Create or retrieve a cached signed URL for secure file access with 1 hour expiration. " +
                     "If a valid cached URL exists (less than 1 hour old), returns the cached URL. " +
                     "Otherwise, creates a new signed URL and updates the cache."
    )
    public ResponseEntity<Map<String, Object>> createSignedUrl(@PathVariable Long fileId) {
        try {
            String signedUrl = cloudStorageService.createSignedUrl(fileId);
            Map<String, Object> response = new HashMap<>();
            response.put("fileId", fileId);
            response.put("signedUrl", signedUrl);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/debug/{fileId}")
    @Operation(
        summary = "Debug file information",
        description = "Get detailed information about a file including all paths and URLs used for debugging purposes"
    )
    public ResponseEntity<Map<String, Object>> debugFileInfo(@PathVariable Long fileId) {
        try {
            FileMetadata metadata = cloudStorageService.getFileMetadata(fileId);

            Map<String, Object> response = new HashMap<>();
            response.put("fileId", metadata.getId());
            response.put("originalFilename", metadata.getOriginalFilename());
            response.put("storedFilename", metadata.getStoredFilename());
            response.put("filePath", metadata.getFilePath());
            response.put("fileUrl", metadata.getFileUrl());
            response.put("cloudProvider", metadata.getCloudProvider());
            response.put("bucketName", metadata.getBucketName());
            response.put("contentType", metadata.getContentType());
            response.put("fileSize", metadata.getFileSize());
            response.put("uploadedAt", metadata.getUploadedAt());
            response.put("isPublic", metadata.getIsPublic());

            // Add what would be used for signed URL
            Map<String, String> signedUrlInfo = new HashMap<>();
            signedUrlInfo.put("filenameUsed", metadata.getStoredFilename());
            signedUrlInfo.put("bucketUsed", metadata.getBucketName());
            signedUrlInfo.put("expectedPath", metadata.getBucketName() + "/" + metadata.getStoredFilename());
            response.put("signedUrlInfo", signedUrlInfo);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get files by user", description = "Retrieve all files uploaded by a specific user")
    public ResponseEntity<List<FileMetadata>> getFilesByUser(@PathVariable Long userId) {
        List<FileMetadata> files = cloudStorageService.getFilesByUser(userId);
        return ResponseEntity.ok(files);
    }

//    @GetMapping("/entity/{entityType}/{entityId}")
//    @Operation(summary = "Get files by entity", description = "Retrieve all files associated with a specific entity (e.g., merchant, user)")
//    public ResponseEntity<List<FileMetadata>> getFilesByEntity(
//            @PathVariable String entityType,
//            @PathVariable Long entityId) {
//        List<FileMetadata> files = cloudStorageService.getFilesByEntity(entityType, entityId);
//        return ResponseEntity.ok(files);
//    }

    @DeleteMapping("/{fileId}")
    @Operation(summary = "Delete file", description = "Delete a file from cloud storage and remove its metadata")
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
