package merchant_manager.repository;

import merchant_manager.models.ContactMerchant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactMerchantRepository extends JpaRepository<ContactMerchant,Long> {
    List<ContactMerchant> findByContactId(Long leadId);
    List<ContactMerchant> findByMerchantId(Long merchantId);
}
