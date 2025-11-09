package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.FieldType;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "template_form")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateForm extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(name = "key", unique = true, nullable = false)
    private String key;

    @Column(name = "label", nullable = false)
    private String label;

    @Enumerated(EnumType.STRING)
    private FieldType type;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private List<Map<String, String>> options;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> formProps;

    @PrePersist
    private void generateKey() {
        if (this.key == null || this.key.isEmpty()) {
            this.key = generateUniqueHash();
        }
    }

    private String generateUniqueHash() {
        // Combine UUID with timestamp and random component for maximum uniqueness
        String uuid = UUID.randomUUID().toString().replace("-", "");
        long timestamp = System.nanoTime();
        int random = (int) (Math.random() * 10000);

        // Create a combined string and hash it
        String combined = uuid + timestamp + random;
        return Integer.toHexString(combined.hashCode()) + uuid.substring(0, 24);
    }
}
