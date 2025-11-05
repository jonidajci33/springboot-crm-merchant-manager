package merchant_manager.repository;

import merchant_manager.models.TemplateFormDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateFormDefaultRepository extends JpaRepository<TemplateFormDefault,Long> {
}
