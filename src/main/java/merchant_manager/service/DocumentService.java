package merchant_manager.service;

import merchant_manager.models.DTO.RecipientDocumentResponseDTO;
import merchant_manager.models.Document;
import merchant_manager.models.enums.RecipientStatus;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document save(Document document);

    Document findById(Long id);

    List<Document> findAll();

    Document createDocument(Document document, MultipartFile file);

    void deleteDocument(Long id);

    RecipientDocumentResponseDTO getRecipientDocumentByToken(String token);

    Document signDocument(String token, RecipientStatus status, MultipartFile file);
}
