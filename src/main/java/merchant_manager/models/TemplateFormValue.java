package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_form_value")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFormValue extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_form_id", nullable = false)
    private TemplateForm templateForm;

    @Column(name = "record_id",  nullable = false)
    private Long recordId;

    @Column(name = "value")
    private String value;

}
