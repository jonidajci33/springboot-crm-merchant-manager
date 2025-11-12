package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.FieldType;
import merchant_manager.models.enums.Plan;

@Entity
@Table(name = "pointing_system")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointingSystem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    private Plan plan;

    @Column(name = "pricing_type")
    private String pricingType;

    @Column(name = "card_fee")
    private Float cardFee;

    @Column(name = "bps")
    private Float bps;
}
