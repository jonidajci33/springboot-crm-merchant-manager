package merchant_manager.repository;

import merchant_manager.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findById(Long id);
    @Query(value = """
            select u from User u where u.accountStatus = "PENDING"
            """)
    List<User> findByAccountStatus();
}
