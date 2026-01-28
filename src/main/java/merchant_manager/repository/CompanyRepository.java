package merchant_manager.repository;

import merchant_manager.models.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {
    @Query("select c from Company c join c.users u where u.id = :userId")
    List<Company> findByUserId(@Param("userId") Long userId);

    @Query("select c from Company c join c.users u where c.id = :id and u.id = :userId")
    Optional<Company> findByIdAndUserId(@Param("id") Long id, @Param("userId") Long userId);

    @Query("select (count(c) > 0) from Company c join c.users u where c.id = :companyId and u.id = :userId")
    boolean existsByIdAndUserId(@Param("companyId") Long companyId, @Param("userId") Long userId);
}
