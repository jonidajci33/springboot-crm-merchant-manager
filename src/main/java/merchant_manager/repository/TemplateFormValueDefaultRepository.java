package merchant_manager.repository;

import merchant_manager.models.TemplateFormValueDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TemplateFormValueDefaultRepository extends JpaRepository<TemplateFormValueDefault,Long> {



}
