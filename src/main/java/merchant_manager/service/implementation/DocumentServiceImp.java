package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.DTO.RecipientDocumentResponseDTO;
import merchant_manager.models.Document;
import merchant_manager.models.FileMetadata;
import merchant_manager.models.Recipient;
import merchant_manager.models.User;
import merchant_manager.models.enums.DocumentStatus;
import merchant_manager.models.enums.RecipientRole;
import merchant_manager.models.enums.RecipientStatus;
import merchant_manager.repository.DocumentRepository;
import merchant_manager.repository.RecipientRepository;
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
    private final RecipientRepository recipientRepository;
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

        // Update audit fields for recipients
        if (document.getRecipients() != null) {
            document.getRecipients().forEach(recipient -> {
                recipient.setCreatedBy(user.getUsername());
                recipient.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                recipient.setLastUpdatedBy(user.getUsername());
                recipient.setDocument(document);
                recipient.setStatus(RecipientStatus.PENDING);
            });
            document.setNrOfRecipient((long) document.getRecipients().size());
        }

        if (document.getFileMetadata() != null) {
            if (document.getFileMetadata().getId() != null) {
                FileMetadata fileMetadata = cloudStorageService.getFileMetadata(document.getFileMetadata().getId());
                if (fileMetadata.getOriginalFilename().equals(file.getOriginalFilename())) {
                    document.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    document.setLastUpdatedBy(user.getUsername());
                    savedDocument = save(document);
                }else{
                    FileMetadata newFile = cloudStorageService.uploadFile(file);
                    document.setCreatedBy(user.getUsername());
                    document.setFileMetadata(newFile);
                    document.setLastUpdatedBy(user.getUsername());
                    document.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
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

    @Override
    public RecipientDocumentResponseDTO getRecipientDocumentByToken(String token) {
        Recipient recipient = recipientRepository.findByToken(token)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recipient not found with token: " + token));

        Document document = recipient.getDocument();
        if (document == null) {
            throw new CustomExceptions.ResourceNotFoundException("No document associated with this recipient");
        }

        return new RecipientDocumentResponseDTO(recipient, document);
    }

    @Override
    public Document signDocument(String token, RecipientStatus status, MultipartFile file) {
        // Find recipient by token
        Recipient recipient = recipientRepository.findByToken(token)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Recipient not found with token: " + token));

        Document document = recipient.getDocument();
        if (document == null) {
            throw new CustomExceptions.ResourceNotFoundException("No document associated with this recipient");
        }

        if (status == RecipientStatus.SIGNED) {
            // Validate that file is provided for signing
            if (file == null || file.isEmpty()) {
                throw new CustomExceptions.ResourceNotFoundException("File is required when signing a document");
            }
            // Update recipient status to SIGNED
            recipient.setStatus(RecipientStatus.SIGNED);
            recipient.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());

            // Replace the document file with the new signed file
            FileMetadata newFile = cloudStorageService.uploadFile(file);
            document.setFileMetadata(newFile);

            // Increment signedNr
            Long currentSigned = document.getSignedNr() != null ? document.getSignedNr() : 0L;
            document.setSignedNr(currentSigned + 1);

            // Check if all signers have signed
            long totalSigners = document.getNrOfRecipient() != null ? document.getNrOfRecipient() : 0L;

            if (document.getSignedNr() >= totalSigners) {
                document.setStatus(DocumentStatus.COMPLETE);
            }

        } else if (status == RecipientStatus.DECLINED) {
            // Update recipient status to DECLINED
            recipient.setStatus(RecipientStatus.DECLINED);
            recipient.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());

            // Increment declinedNr
            Long currentDeclined = document.getDeclinedNr() != null ? document.getDeclinedNr() : 0L;
            document.setDeclinedNr(currentDeclined + 1);

            // Set document status to DECLINED
            document.setStatus(DocumentStatus.DECLINED);
        }

        // Update document audit fields
        document.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());

        // Save recipient and document
        recipientRepository.save(recipient);
        return documentRepository.save(document);
    }
}
