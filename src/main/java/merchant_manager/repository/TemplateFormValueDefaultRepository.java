package merchant_manager.repository;

import merchant_manager.models.TemplateFormValueDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TemplateFormValueDefaultRepository extends JpaRepository<TemplateFormValueDefault,Long>,
        JpaSpecificationExecutor<TemplateFormValueDefault> {

    List<TemplateFormValueDefault> findByRecordId(Long recordId);

    @Query("SELECT DISTINCT tfvd.recordId FROM TemplateFormValueDefault tfvd " +
           "WHERE tfvd.templateFormDefault.template.id = :templateId " +
           "ORDER BY tfvd.recordId DESC")
    List<Long> findDistinctRecordIdsByTemplateId(@Param("templateId") Long templateId);

    @Query("SELECT tfvd FROM TemplateFormValueDefault tfvd " +
           "WHERE tfvd.templateFormDefault.template.id = :templateId " +
           "AND tfvd.recordId IN :recordIds")
    List<TemplateFormValueDefault> findByTemplateIdAndRecordIds(
            @Param("templateId") Long templateId,
            @Param("recordIds") List<Long> recordIds);

    TemplateFormValueDefault findByTemplateFormDefaultIdAndRecordId(Long templateFormId, Long recordId);

    @Query("SELECT tfvd FROM TemplateFormValueDefault tfvd " +
           "WHERE tfvd.templateFormDefault.template.menu.id = :menuId " +
           "AND tfvd.recordId = :recordId")
    List<TemplateFormValueDefault> findByMenuIdAndRecordId(
            @Param("menuId") Long menuId,
            @Param("recordId") Long recordId);
}
