package merchant_manager.repository;

import merchant_manager.models.TemplateForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateFormRepository extends JpaRepository<TemplateForm,Long> {

    List<TemplateForm> findByTemplateIdOrderByPriorityAsc(Long templateId);

    void deleteByIdAndTemplateId(Long id, Long templateId);

    Optional<TemplateForm> findByKey(String key);

    void deleteByKey(String key);

}
