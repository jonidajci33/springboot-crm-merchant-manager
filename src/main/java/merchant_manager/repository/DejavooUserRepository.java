package merchant_manager.repository;

import merchant_manager.models.DejavooUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DejavooUserRepository extends JpaRepository<DejavooUser, Long> {
    Optional<DejavooUser> findByUsername(String username);
    Optional<DejavooUser> findByMerchantId(Long merchantId);
}
