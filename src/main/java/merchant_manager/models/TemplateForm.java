package merchant_manager.models;

import com.vladmihalcea.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.FieldType;
import org.hibernate.annotations.Type;

import java.util.Map;

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

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private Template template;

    @Column(name = "key", unique = true, nullable = false)
    private String key;

    @Enumerated(EnumType.STRING)
    private FieldType type;

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, String> options;

    @Column(name = "priority", unique = true, nullable = false)
    private Long priority;
}
