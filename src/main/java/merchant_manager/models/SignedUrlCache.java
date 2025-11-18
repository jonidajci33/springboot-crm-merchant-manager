package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "signed_url_cache", indexes = {
    @Index(name = "idx_file_metadata_id", columnList = "file_metadata_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignedUrlCache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id", unique = true)
    private FileMetadata fileMetadata;

    @Column(name = "signed_url", columnDefinition = "TEXT")
    private String signedUrl;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (expiresAt == null) {
            expiresAt = createdAt.plusHours(1);
        }
    }

    /**
     * Check if the signed URL is still valid
     * @return true if the URL has not expired yet
     */
    public boolean isValid() {
        return LocalDateTime.now().isBefore(expiresAt);
    }
}
