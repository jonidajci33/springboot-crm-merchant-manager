package merchant_manager.repository;

import merchant_manager.models.TemplateFormDefault;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TemplateFormDefaultRepository extends JpaRepository<TemplateFormDefault,Long> {

    List<TemplateFormDefault> findByTemplateId(Long templateId);

    Optional<TemplateFormDefault> findByKey(String key);

    Optional<TemplateFormDefault> findByTemplateIdAndSearchContact(Long templateId, Boolean searchCustomer);

    Optional<TemplateFormDefault> findByTemplateIdAndSearchMerchant(Long templateId, Boolean searchMerchant);

//    @Query("SELECT tfd FROM TemplateFormDefault tfd WHERE tfd.template.id = :templateId")
//    List<TemplateFormDefault> findAllByTemplateId(@Param("templateId") Long templateId);

    @Modifying
    @Transactional
    void deleteByKey(String key);
}
