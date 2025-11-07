package merchant_manager.repository;

import merchant_manager.models.TemplateFormDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateFormDefaultRepository extends JpaRepository<TemplateFormDefault,Long> {

    List<TemplateFormDefault> findByTemplateId(Long templateId);

    Optional<TemplateFormDefault> findByKey(String key);

    @Query("SELECT tfd FROM TemplateFormDefault tfd WHERE tfd.template.id = :templateId")
    List<TemplateFormDefault> findAllByTemplateId(@Param("templateId") Long templateId);

    Optional<TemplateFormDefault> removeByKey(String key);
}
