package merchant_manager.repository;

import merchant_manager.models.Company;
import merchant_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    List<Company> findByUser(User user);
    List<Company> findByUserId(Long userId);
    Optional<Company> findByIdAndUserId(Long id, Long userId);
    Optional<Company> findById(Long id);
}
