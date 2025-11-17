package merchant_manager.models;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

// Define a base model with common fields and behaviors
// Common fields for all models
@MappedSuperclass
@Data
@AllArgsConstructor
public abstract class BaseModel {

    @Column(name = "created_at", nullable = false, updatable = false)
    protected LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    protected LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false)
    protected String createdBy;

    @Column(name = "last_updated_by")
    protected String lastUpdatedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime();
    }

    // Constructor
    public BaseModel() {

    }
}
