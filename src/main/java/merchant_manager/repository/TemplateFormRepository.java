package merchant_manager.repository;

import merchant_manager.models.TemplateForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateFormRepository extends JpaRepository<TemplateForm,Long> {
}
