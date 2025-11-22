package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.Document;
import merchant_manager.models.FileMetadata;
import merchant_manager.models.User;
import merchant_manager.repository.DocumentRepository;
import merchant_manager.service.DocumentService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DocumentServiceImp implements DocumentService {

    private final DocumentRepository documentRepository;
    private final CloudStorageServiceImp cloudStorageService;
    private final UserServiceImp userServiceImp;

    @Override
    public Document save(Document document) {
        return documentRepository.save(document);
    }

    @Override
    public Document findById(Long id) {
        return documentRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Document not found"));
    }

    @Override
    public List<Document> findAll() {
        return documentRepository.findAll();
    }

    @Override
    public Document createDocument(Document document, MultipartFile file) {
        Document savedDocument = null;
        User user = userServiceImp.getLoggedUser();

        if (document.getFileMetadata() != null) {
            if (document.getFileMetadata().getId() != null) {
                FileMetadata fileMetadata = cloudStorageService.getFileMetadata(document.getFileMetadata().getId());
                if (fileMetadata.getOriginalFilename().equals(file.getOriginalFilename())) {
                    document.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    document.setLastUpdatedBy(user.getUsername());
                    savedDocument = save(document);
                }
            }
        } else {
            FileMetadata newFile = cloudStorageService.uploadFile(file);
            document.setFileMetadata(newFile);
            document.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            document.setCreatedBy(user.getUsername());
            document.setLastUpdatedBy(user.getUsername());
            document.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            savedDocument = save(document);
        }

        return savedDocument;
    }

    @Override
    public void deleteDocument(Long id) {
        documentRepository.deleteById(id);
    }
}
