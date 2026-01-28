package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import merchant_manager.models.Document;
import merchant_manager.models.Recipient;
import merchant_manager.models.enums.RecipientRole;
import merchant_manager.models.enums.RecipientStatus;
import merchant_manager.repository.DocumentRepository;
import merchant_manager.repository.RecipientRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test controller for E-Signature functionality.
 * This controller provides endpoints for testing the document signing flow.
 * Remove or disable this in production.
 */
@RestController
@RequestMapping("/api/esign-test")
@Tag(name = "E-Sign Test", description = "Test endpoints for e-signature functionality (development only)")
@AllArgsConstructor
public class ESignTestController {

    private final DocumentRepository documentRepository;
    private final RecipientRepository recipientRepository;

    /**
     * Get all hardcoded test tokens for documents.
     * This helps identify which tokens to use for testing.
     */
    @GetMapping("/tokens")
    @Operation(summary = "Get test tokens", description = "Retrieve all available test tokens from recipients")
    public ResponseEntity<Map<String, Object>> getTestTokens() {
        List<Document> documents = documentRepository.findAll();
        Map<String, Object> response = new HashMap<>();

        documents.forEach(doc -> {
            Map<String, Object> docInfo = new HashMap<>();
            Map<String, String> tokens = new HashMap<>();

            if (doc.getRecipients() != null) {
                doc.getRecipients().forEach(recipient -> {
                    tokens.put(
                        recipient.getName() + " (" + recipient.getEmail() + ")",
                        recipient.getToken()
                    );
                });
            }

            if (!tokens.isEmpty()) {
                docInfo.put("documentName", doc.getName());
                docInfo.put("documentId", doc.getId());
                docInfo.put("documentStatus", doc.getStatus());
                docInfo.put("recipients", tokens);
                response.put("Document_" + doc.getId(), docInfo);
            }
        });

        return ResponseEntity.ok(response);
    }

    /**
     * Get a specific token by document ID and recipient email (for easier testing)
     */
    @GetMapping("/token/{documentId}/{recipientEmail}")
    @Operation(summary = "Get test token by document and email", description = "Retrieve a specific token by document ID and recipient email")
    public ResponseEntity<Map<String, String>> getTestToken(
            @PathVariable Long documentId,
            @PathVariable String recipientEmail) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Recipient recipient = document.getRecipients().stream()
                .filter(r -> r.getEmail().equals(recipientEmail))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        Map<String, String> response = new HashMap<>();
        response.put("token", recipient.getToken());
        response.put("documentName", document.getName());
        response.put("recipientName", recipient.getName());
        response.put("recipientEmail", recipient.getEmail());
        response.put("signingUrl", "http://localhost:3000/sign/" + recipient.getToken());

        return ResponseEntity.ok(response);
    }

    /**
     * Force create a test recipient with a hardcoded token for development/testing
     * Use this to create a recipient with a known token without waiting for email
     */
    @PostMapping("/create-test-recipient/{documentId}")
    @Operation(summary = "Create test recipient with hardcoded token", description = "Create a test recipient with a specific hardcoded token (development only)")
    public ResponseEntity<Map<String, Object>> createTestRecipient(
            @PathVariable Long documentId,
            @RequestParam(defaultValue = "test-signer") String testToken) {

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        Recipient recipient = new Recipient();
        recipient.setName("Test Signer");
        recipient.setEmail("test-signer@example.com");
        recipient.setRole(RecipientRole.SIGNER);
        recipient.setStatus(RecipientStatus.PENDING);
        recipient.setToken(testToken); // Set hardcoded token for testing
        recipient.setDocument(document);

        Recipient savedRecipient = recipientRepository.save(recipient);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Test recipient created successfully");
        response.put("token", savedRecipient.getToken());
        response.put("documentId", document.getId());
        response.put("documentName", document.getName());
        response.put("signingUrl", "http://localhost:3000/sign/" + savedRecipient.getToken());

        return ResponseEntity.ok(response);
    }

    /**
     * Get signer info from token (useful for debugging)
     */
    @GetMapping("/signer/{token}")
    @Operation(summary = "Get signer info from token", description = "Retrieve signer and document information for a given token")
    public ResponseEntity<Map<String, Object>> getSignerInfo(@PathVariable String token) {
        Recipient recipient = recipientRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Token not found"));

        Map<String, Object> response = new HashMap<>();
        response.put("signerName", recipient.getName());
        response.put("signerEmail", recipient.getEmail());
        response.put("signerRole", recipient.getRole());
        response.put("signerStatus", recipient.getStatus());
        response.put("documentName", recipient.getDocument().getName());
        response.put("documentId", recipient.getDocument().getId());
        response.put("documentStatus", recipient.getDocument().getStatus());
        response.put("nrOfRecipients", recipient.getDocument().getNrOfRecipient());
        response.put("signedNr", recipient.getDocument().getSignedNr());

        return ResponseEntity.ok(response);
    }
}
