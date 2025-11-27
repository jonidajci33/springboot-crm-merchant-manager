package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.dto.ContactMerchantRequest;
import merchant_manager.dto.ContactMerchantWithDetailsDTO;
import merchant_manager.models.ContactMerchant;
import merchant_manager.service.implementation.ContactMerchantServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contact-merchant")
@Tag(name = "Contacts", description = "Manage contacts and merchant-contact relationships")
public class ContactMerchantController {

    private final ContactMerchantServiceImp contactMerchantService;

    public ContactMerchantController(ContactMerchantServiceImp contactMerchantService) {
        this.contactMerchantService = contactMerchantService;
    }

    @PostMapping
    @Operation(summary = "Create contact-merchant relationships", description = "Create one or more contact-merchant relationships")
    public ResponseEntity<List<ContactMerchant>> createContactMerchant(@RequestBody List<ContactMerchantRequest> request) {
        List<ContactMerchant> saved = contactMerchantService.createContactMerchant(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get contact-merchant by ID", description = "Retrieve a specific contact-merchant relationship by ID")
    public ResponseEntity<ContactMerchant> getContactMerchantById(@PathVariable Long id) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantById(id));
    }

    @GetMapping
    @Operation(summary = "Get all contact-merchants", description = "Retrieve all contact-merchant relationships")
    public ResponseEntity<List<ContactMerchant>> getAllContactMerchants() {
        return ResponseEntity.ok(contactMerchantService.getAllContactMerchants());
    }

    @GetMapping("/contact/{contactId}")
    @Operation(summary = "Get merchants by contact", description = "Retrieve all merchants associated with a specific contact")
    public ResponseEntity<List<ContactMerchant>> getContactMerchantsByContactId(@PathVariable Long contactId) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantsByLeadId(contactId));
    }

//    @GetMapping("/contact/{contactId}/with-details")
//    @Operation(summary = "Get merchants by contact with full details", description = "Retrieve all merchants associated with a specific contact, including dynamic field values")
//    public ResponseEntity<List<ContactMerchantWithDetailsDTO>> getContactMerchantsWithDetails(@PathVariable Long contactId) {
//        return ResponseEntity.ok(contactMerchantService.getContactMerchantsWithDetails(contactId));
//    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get contacts by merchant", description = "Retrieve all contacts associated with a specific merchant")
    public ResponseEntity<List<ContactMerchant>> getContactMerchantsByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(contactMerchantService.getContactMerchantsByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete contact-merchant relationship", description = "Delete a contact-merchant relationship by ID")
    public ResponseEntity<Void> deleteContactMerchant(@PathVariable Long id) {
        contactMerchantService.deleteContactMerchant(id);
        return ResponseEntity.noContent().build();
    }
}
