package merchant_manager.models;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.io.File;
import java.util.Map;

@Entity
@Data
@Table(name="esign_template")
@AllArgsConstructor
@NoArgsConstructor
public class EsignTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", unique = true, length = 100)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @OneToOne
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id")
    private FileMetadata fileMetadata;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> fields;
}
