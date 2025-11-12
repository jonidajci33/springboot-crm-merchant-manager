package merchant_manager.controller;

import merchant_manager.dto.ContactMerchantRequest;
import merchant_manager.models.ContactMerchant;
import merchant_manager.service.implementation.ContactMerchantServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact-merchant")
public class ContactMerchantController {

    private final ContactMerchantServiceImp contactMerchantService;

    public ContactMerchantController(ContactMerchantServiceImp contactMerchantService) {
        this.contactMerchantService = contactMerchantService;
    }

    @PostMapping
    public ResponseEntity<List<ContactMerchant>> createContactMerchant(@RequestBody List<ContactMerchantRequest> request) {
        List<ContactMerchant> saved = contactMerchantService.createContactMerchant(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMerchant> getContactMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantById(id));
    }

    @GetMapping
    public ResponseEntity<List<ContactMerchant>> getAllContactMerchants() {
        return ResponseEntity.ok(contactMerchantService.getAllContactMerchants());
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<List<ContactMerchant>> getContactMerchantsByLeadId(@PathVariable Long leadId) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantsByLeadId(leadId));
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<ContactMerchant>> getContactMerchantsByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantsByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContactMerchant(@PathVariable Long id) {
        contactMerchantService.deleteContactMerchant(id);
        return ResponseEntity.noContent().build();
    }
}
