package merchant_manager.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "merchant_miles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantMiles extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    @Column(name = "points")
    private Long points;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pointing_system_id", referencedColumnName = "id")
    private PointingSystem pointingSystem;

    @JsonIgnore
    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

}
