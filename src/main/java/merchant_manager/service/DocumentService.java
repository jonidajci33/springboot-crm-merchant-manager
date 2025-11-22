package merchant_manager.service;

import merchant_manager.models.Document;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    Document save(Document document);

    Document findById(Long id);

    List<Document> findAll();

    Document createDocument(Document document, MultipartFile file);

    void deleteDocument(Long id);
}
