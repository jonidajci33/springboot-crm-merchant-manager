package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.models.DTO.RecipientDocumentResponseDTO;
import merchant_manager.models.Document;
import merchant_manager.models.enums.RecipientStatus;
import merchant_manager.service.implementation.DocumentServiceImp;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@Tag(name = "Documents", description = "APIs for managing documents and signatures")
public class DocumentController {

    private final DocumentServiceImp documentService;

    public DocumentController(DocumentServiceImp documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create document with file", description = "Create a new document with file upload")
    public ResponseEntity<Document> createDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("document") Document document) {

        Document created = documentService.createDocument(document, file);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get document by ID", description = "Retrieve a document by its ID")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        Document document = documentService.findById(id);
        return ResponseEntity.ok(document);
    }

    @GetMapping
    @Operation(summary = "Get all documents", description = "Retrieve all documents")
    public ResponseEntity<List<Document>> getAllDocuments() {
        List<Document> documents = documentService.findAll();
        return ResponseEntity.ok(documents);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete document", description = "Delete a document by ID")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        documentService.deleteDocument(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/recipient/{token}")
    @Operation(summary = "Get recipient and document by token", description = "Retrieve recipient and their associated document using the recipient's token")
    public ResponseEntity<RecipientDocumentResponseDTO> getRecipientDocumentByToken(@PathVariable String token) {
        RecipientDocumentResponseDTO response = documentService.getRecipientDocumentByToken(token);
        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/sign", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Sign or decline a document", description = "Sign or decline a document using recipient token. If signing, upload the signed file. If declining, file is optional.")
    public ResponseEntity<Document> signDocument(
            @RequestPart("token") String token,
            @RequestPart("status") RecipientStatus status,
            @RequestPart("file") MultipartFile file) {

        Document updatedDocument = documentService.signDocument(token, status, file);
        return ResponseEntity.ok(updatedDocument);
    }
}
