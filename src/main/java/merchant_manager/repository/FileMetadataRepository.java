package merchant_manager.repository;

import merchant_manager.models.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileMetadataRepository extends JpaRepository<FileMetadata,Long> {
    List<FileMetadata> findByUploadedById(Long userId);
//    List<FileMetadata> findByEntityTypeAndEntityId(String entityType, Long entityId);
    List<FileMetadata> findByStoredFilename(String storedFilename);
}
