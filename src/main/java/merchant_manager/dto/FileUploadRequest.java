package merchant_manager.dto;

import lombok.Data;

@Data
public class FileUploadRequest {
    private String entityType;
    private Long entityId;
    private Boolean isPublic;
}
