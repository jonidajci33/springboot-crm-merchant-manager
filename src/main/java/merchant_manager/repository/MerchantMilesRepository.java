package merchant_manager.repository;

import merchant_manager.models.MerchantMiles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MerchantMilesRepository extends JpaRepository<MerchantMiles,Long> {
    List<MerchantMiles> findByMerchantId(Long merchantId);
}
