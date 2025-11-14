package merchant_manager.repository;

import merchant_manager.models.DejavooCredentials;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DejavooCredentialsRepository extends JpaRepository<DejavooCredentials,Long> {
    Optional<DejavooCredentials> findByMerchantId(Long merchantId);
}
