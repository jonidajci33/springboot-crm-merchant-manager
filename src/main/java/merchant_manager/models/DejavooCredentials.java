package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "dejavoo_credentials")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DejavooCredentials {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "merchant_id", referencedColumnName = "id")
    private Merchant merchant;

    @Column(name = "register_id")
    private String registerId;

    @Column(name = "auth_key")
    private String authKey;

    @Column(name = "api_url")
    private String apiUrl;

    @Column(name = "is_active")
    private Boolean isActive;

}
