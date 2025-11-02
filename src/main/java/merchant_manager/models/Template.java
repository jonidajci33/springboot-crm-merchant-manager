package merchant_manager.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "template")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Template extends BaseModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)  // foreign key column in template table
    private User user;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false)  // foreign key column in template table
    private Menu menu;

    public Template(LocalDateTime createdAt, LocalDateTime updatedAt, String createdBy, String lastUpdatedBy, Menu menu, User user) {
        super(createdAt, updatedAt, createdBy, lastUpdatedBy);
        this.menu = menu;
        this.user = user;
    }
}
