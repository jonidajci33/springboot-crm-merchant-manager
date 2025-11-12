package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Lead extends BaseModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "is_signed")
    private Boolean isSigned;

    @Column(name = "is_active")
    private Boolean isActive;

}
