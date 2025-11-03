package merchant_manager.repository;
import merchant_manager.models.Template;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<Template,Long> {

    Optional<Template> findByUserIdAndMenuId(Long userId, Long menuId);

}
