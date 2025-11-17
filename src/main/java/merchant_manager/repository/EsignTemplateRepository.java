package merchant_manager.repository;

import merchant_manager.models.EsignTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EsignTemplateRepository extends JpaRepository<EsignTemplate, Long> {

    Optional<EsignTemplate> findById(Long id);
    Optional<List<EsignTemplate>> findAllByMerchantId(Long merchantId);
}
