package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "template_form_value_default")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TemplateFormValueDefault extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_form_default_id", nullable = false)
    private TemplateFormDefault templateFormDefault;

    @Column(name = "record_id",  nullable = false)
    private Long recordId;

    @Column(name = "value")
    private String value;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // foreign key column in template table
    private User user;
}
