package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.RecipientRole;
import merchant_manager.models.enums.RecipientStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.security.SecureRandom;

@Entity
@Table(name = "recipient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipient extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private RecipientRole role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RecipientStatus status;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "document_id", referencedColumnName = "id")
    private Document document;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recipient_fields", columnDefinition = "jsonb")
    private String recipientFields;

    @Column(name = "token", unique = true, nullable = false, length = 32)
    private String token;

    @PrePersist
    private void generateToken() {
        if (this.token == null) {
            SecureRandom random = new SecureRandom();
            byte[] bytes = new byte[16]; // 128 bits
            random.nextBytes(bytes);

            // Convert to hex string (32 characters)
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            this.token = sb.toString();
        }
    }

}
