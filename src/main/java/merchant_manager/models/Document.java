package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.DocumentStatus;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

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
    @JdbcTypeCode(SqlTypes.NAMED_ENUM)
    @Column(name = "status", nullable = false)
    private DocumentStatus status;

    @OneToMany(mappedBy = "document", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Recipient> recipients;

    @OneToOne
    @JoinColumn(name = "file_metadata_id", referencedColumnName = "id")
    private FileMetadata fileMetadata;

    @Column(name = "nr_of_recipient")
    private Long nrOfRecipient;

    @Column(name = "signed_nr")
    private Long signedNr;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "esign_template_id", referencedColumnName = "id")
    private EsignTemplate esignTemplate;

}
