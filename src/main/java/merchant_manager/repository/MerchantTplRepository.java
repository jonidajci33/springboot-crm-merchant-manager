package merchant_manager.repository;

import merchant_manager.models.MerchantTpl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantTplRepository extends JpaRepository<MerchantTpl, Long> {
    List<MerchantTpl> findByMerchantId(Long merchantId);
}
