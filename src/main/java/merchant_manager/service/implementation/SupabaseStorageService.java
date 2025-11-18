package merchant_manager.service.implementation;

import merchant_manager.config.CloudStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Supabase Storage Service
 * Uses Supabase Storage REST API for file operations
 */
@Service
public class SupabaseStorageService {

    private static final Logger logger = LoggerFactory.getLogger(SupabaseStorageService.class);

    private final CloudStorageProperties storageProperties;
    private final RestTemplate restTemplate;

    public SupabaseStorageService(CloudStorageProperties storageProperties, RestTemplate restTemplate) {
        this.storageProperties = storageProperties;
        this.restTemplate = restTemplate;
    }

    /**
     * Upload file to Supabase Storage
     * Endpoint: POST /storage/v1/object/{bucket}/{path}
     */
    public String uploadFile(MultipartFile file, String filename) {
        try {
            String uploadUrl = String.format("%s/storage/v1/object/%s/%s",
                    storageProperties.getEndpoint(),
                    storageProperties.getBucketName(),
                    filename);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + storageProperties.getSecretKey());
            headers.setContentType(MediaType.parseMediaType(file.getContentType()));

            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    uploadUrl,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK || response.getStatusCode() == HttpStatus.CREATED) {
                // Generate public URL
                String publicUrl = String.format("%s/storage/v1/object/public/%s/%s",
                        storageProperties.getEndpoint(),
                        storageProperties.getBucketName(),
                        filename);

                logger.info("File uploaded to Supabase: {}", filename);
                return publicUrl;
            } else {
                throw new RuntimeException("Failed to upload file to Supabase. Status: " + response.getStatusCode());
            }

        } catch (IOException e) {
            logger.error("Error uploading file to Supabase", e);
            throw new RuntimeException("Failed to upload file to Supabase", e);
        }
    }

    /**
     * Download file from Supabase Storage
     * Endpoint: GET /storage/v1/object/{bucket}/{path}
     */
    public InputStream downloadFile(String filename) {
        try {
            String downloadUrl = String.format("%s/storage/v1/object/%s/%s",
                    storageProperties.getEndpoint(),
                    storageProperties.getBucketName(),
                    filename);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + storageProperties.getSecretKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<byte[]> response = restTemplate.exchange(
                    downloadUrl,
                    HttpMethod.GET,
                    entity,
                    byte[].class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                logger.info("File downloaded from Supabase: {}", filename);
                return new ByteArrayInputStream(response.getBody());
            } else {
                throw new RuntimeException("Failed to download file from Supabase");
            }

        } catch (Exception e) {
            logger.error("Error downloading file from Supabase", e);
            throw new RuntimeException("Failed to download file from Supabase", e);
        }
    }

    /**
     * Delete file from Supabase Storage
     * Endpoint: DELETE /storage/v1/object/{bucket}/{path}
     */
    public void deleteFile(String filename) {
        try {
            String deleteUrl = String.format("%s/storage/v1/object/%s/%s",
                    storageProperties.getEndpoint(),
                    storageProperties.getBucketName(),
                    filename);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + storageProperties.getSecretKey());

            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    deleteUrl,
                    HttpMethod.DELETE,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("File deleted from Supabase: {}", filename);
            } else {
                throw new RuntimeException("Failed to delete file from Supabase. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error deleting file from Supabase", e);
            throw new RuntimeException("Failed to delete file from Supabase", e);
        }
    }

    /**
     * Create signed URL for private file access
     * Endpoint: POST /storage/v1/object/sign/{bucket}/{path}
     */
    public String createSignedUrl(String filename, int expiresInSeconds) {
        try {
            String signUrl = String.format("%s/storage/v1/object/sign/%s/%s",
                    storageProperties.getEndpoint(),
                    storageProperties.getBucketName(),
                    filename);

            logger.debug("Creating signed URL - Endpoint: {}, Bucket: {}, Filename: {}",
                    storageProperties.getEndpoint(),
                    storageProperties.getBucketName(),
                    filename);
            logger.info("Full sign URL: {}", signUrl);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + storageProperties.getSecretKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> body = Map.of("expiresIn", expiresInSeconds);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    signUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            logger.debug("Response status: {}, Body: {}", response.getStatusCode(), response.getBody());

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                String signedPath = (String) response.getBody().get("signedURL");
                logger.info(signedPath);
                if (signedPath == null) {
                    logger.error("signedURL not found in response body: {}", response.getBody());
                    throw new RuntimeException("signedURL not found in response");
                }
                String signedUrl = storageProperties.getEndpoint()+ "/storage/v1" + signedPath;
                logger.info("Created signed URL for file: {}", filename);
                return signedUrl;
            } else {
                logger.error("Failed to create signed URL. Status: {}, Body: {}",
                        response.getStatusCode(), response.getBody());
                throw new RuntimeException("Failed to create signed URL: " + response.getBody());
            }

        } catch (Exception e) {
            logger.error("Error creating signed URL for file: {}. Error: {}", filename, e.getMessage(), e);
            throw new RuntimeException("Failed to create signed URL for file: " + filename + ". Error: " + e.getMessage(), e);
        }
    }

    /**
     * Get public URL for a file
     */
    public String getPublicUrl(String filename) {
        return String.format("%s/storage/v1/object/public/%s/%s",
                storageProperties.getEndpoint(),
                storageProperties.getBucketName(),
                filename);
    }

    /**
     * Move/rename a file
     * Endpoint: POST /storage/v1/object/move
     */
    public void moveFile(String fromPath, String toPath) {
        try {
            String moveUrl = String.format("%s/storage/v1/object/move",
                    storageProperties.getEndpoint());

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + storageProperties.getSecretKey());
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of(
                    "bucketId", storageProperties.getBucketName(),
                    "sourceKey", fromPath,
                    "destinationKey", toPath
            );

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    moveUrl,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            if (response.getStatusCode() == HttpStatus.OK) {
                logger.info("File moved from {} to {}", fromPath, toPath);
            } else {
                throw new RuntimeException("Failed to move file. Status: " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("Error moving file", e);
            throw new RuntimeException("Failed to move file", e);
        }
    }
}
