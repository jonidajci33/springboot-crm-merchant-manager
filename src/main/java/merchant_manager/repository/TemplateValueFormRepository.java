package merchant_manager.repository;

import merchant_manager.models.TemplateFormValue;
import merchant_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateValueFormRepository extends JpaRepository<TemplateFormValue,Long> {

    Optional<TemplateFormValue> findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId);

    @Query("SELECT tfv FROM TemplateFormValue tfv " +
           "WHERE tfv.templateForm.template.menu.id = :menuId " +
           "AND tfv.recordId = :recordId")
    List<TemplateFormValue> findByMenuIdAndRecordId(
            @Param("menuId") Long menuId,
            @Param("recordId") Long recordId);

    @Modifying
    @Transactional
    @Query("DELETE FROM TemplateFormValueDefault tfvd " +
            "WHERE tfvd.templateFormDefault.template.menu.id = :menuId " +
            "AND tfvd.recordId = :recordId ")
    void deleteByMenuIdAndRecordI(@Param("menuId") Long menuId,
                                          @Param("recordId") Long recordId);
}
