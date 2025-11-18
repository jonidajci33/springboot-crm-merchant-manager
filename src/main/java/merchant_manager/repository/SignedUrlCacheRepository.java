package merchant_manager.repository;

import merchant_manager.models.SignedUrlCache;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SignedUrlCacheRepository extends JpaRepository<SignedUrlCache, Long> {

    /**
     * Find a cached signed URL by file metadata ID
     * @param fileMetadataId the file metadata ID
     * @return Optional containing the cached signed URL if found
     */
    Optional<SignedUrlCache> findByFileMetadataId(Long fileMetadataId);
}
