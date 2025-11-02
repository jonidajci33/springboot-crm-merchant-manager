package merchant_manager.repository;

import merchant_manager.models.TemplateFormValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateValueFormRepository extends JpaRepository<TemplateFormValue,Long> {
}
