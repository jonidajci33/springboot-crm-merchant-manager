package merchant_manager.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "cloud.storage")
@Data
public class CloudStorageProperties {

    private String provider; // "aws", "azure", "gcp", "local"
    private String bucketName;
    private String region;
    private String accessKey;
    private String secretKey;
    private String endpoint;
    private String localStoragePath;
    private Long maxFileSize = 10485760L; // 10MB default
}
