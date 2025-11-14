package merchant_manager.controller;

import merchant_manager.models.DejavooCredentials;
import merchant_manager.service.implementation.DejavooCredentialsServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dejavoo-credentials")
public class DejavooCredentialsController {

    private final DejavooCredentialsServiceImp dejavooCredentialsService;

    public DejavooCredentialsController(DejavooCredentialsServiceImp dejavooCredentialsService) {
        this.dejavooCredentialsService = dejavooCredentialsService;
    }

    @PostMapping
    public ResponseEntity<DejavooCredentials> createDejavooCredentials(@RequestBody DejavooCredentials credentials) {
        DejavooCredentials saved = dejavooCredentialsService.save(credentials);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DejavooCredentials> updateDejavooCredentials(@PathVariable Long id, @RequestBody DejavooCredentials credentials) {
        credentials.setId(id);
        DejavooCredentials updated = dejavooCredentialsService.save(credentials);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DejavooCredentials> getDejavooCredentialsById(@PathVariable Long id) {
        return ResponseEntity.ok(dejavooCredentialsService.getDejavooCredentialsById(id));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<DejavooCredentials> getDejavooCredentialsByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(dejavooCredentialsService.getDejavooCredentialsByMerchantId(merchantId));
    }

    @GetMapping
    public ResponseEntity<List<DejavooCredentials>> getAllDejavooCredentials() {
        return ResponseEntity.ok(dejavooCredentialsService.getAllDejavooCredentials());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDejavooCredentials(@PathVariable Long id) {
        dejavooCredentialsService.deleteDejavooCredentials(id);
        return ResponseEntity.noContent().build();
    }
}
