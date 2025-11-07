package merchant_manager.repository;

import merchant_manager.models.TemplateDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateDefaultRepository extends JpaRepository<TemplateDefault, Long> {

    Optional<TemplateDefault> findByMenuId(Long menuId);

}
