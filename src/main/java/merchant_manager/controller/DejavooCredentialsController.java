package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.models.DejavooCredentials;
import merchant_manager.service.implementation.DejavooCredentialsServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dejavoo-credentials")
@Tag(name = "Dejavoo", description = "Manage Dejavoo payment processor credentials and transaction integration")
public class DejavooCredentialsController {

    private final DejavooCredentialsServiceImp dejavooCredentialsService;

    public DejavooCredentialsController(DejavooCredentialsServiceImp dejavooCredentialsService) {
        this.dejavooCredentialsService = dejavooCredentialsService;
    }

    @PostMapping
    @Operation(summary = "Create Dejavoo credentials", description = "Store Dejavoo API credentials for a merchant")
    public ResponseEntity<DejavooCredentials> createDejavooCredentials(@RequestBody DejavooCredentials credentials) {
        DejavooCredentials saved = dejavooCredentialsService.save(credentials);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update Dejavoo credentials", description = "Update existing Dejavoo credentials")
    public ResponseEntity<DejavooCredentials> updateDejavooCredentials(@PathVariable Long id, @RequestBody DejavooCredentials credentials) {
        credentials.setId(id);
        DejavooCredentials updated = dejavooCredentialsService.save(credentials);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get credentials by ID", description = "Retrieve Dejavoo credentials by ID")
    public ResponseEntity<DejavooCredentials> getDejavooCredentialsById(@PathVariable Long id) {
        return ResponseEntity.ok(dejavooCredentialsService.getDejavooCredentialsById(id));
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get credentials by merchant", description = "Retrieve Dejavoo credentials for a specific merchant")
    public ResponseEntity<DejavooCredentials> getDejavooCredentialsByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(dejavooCredentialsService.getDejavooCredentialsByMerchantId(merchantId));
    }

    @GetMapping
    @Operation(summary = "Get all credentials", description = "Retrieve all Dejavoo credentials")
    public ResponseEntity<List<DejavooCredentials>> getAllDejavooCredentials() {
        return ResponseEntity.ok(dejavooCredentialsService.getAllDejavooCredentials());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete credentials", description = "Delete Dejavoo credentials by ID")
    public ResponseEntity<Void> deleteDejavooCredentials(@PathVariable Long id) {
        dejavooCredentialsService.deleteDejavooCredentials(id);
        return ResponseEntity.noContent().build();
    }
}
