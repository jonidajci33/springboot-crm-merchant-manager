package merchant_manager.repository;

import merchant_manager.models.PointingSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointingSystemRepository extends JpaRepository<PointingSystem,Long> {
}
