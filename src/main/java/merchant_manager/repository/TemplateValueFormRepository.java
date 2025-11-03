package merchant_manager.repository;

import merchant_manager.models.TemplateFormValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateValueFormRepository extends JpaRepository<TemplateFormValue,Long> {

    Optional<TemplateFormValue> findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId);
}
