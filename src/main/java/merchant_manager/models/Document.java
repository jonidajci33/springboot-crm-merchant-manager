package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.DocumentStatus;
import org.hibernate.annotations.Type;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "document")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Recipient> recipients;

    @OneToOne
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id")
    private FileMetadata fileMetadata;

    @Column(name = "nr_of_recipient")
    private Long nrOfRecipient;

    @Column(name = "signed_nr")
    private Long signedNr;

    @Column(name = "declined_nr")
    private Long declinedNr;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ManyToOne
    @JoinColumn(name = "esign_template_id", referencedColumnName = "id")
    private EsignTemplate esignTemplate;


    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> fields;

}
