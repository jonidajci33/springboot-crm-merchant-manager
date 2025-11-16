package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.dto.DejavooUserResponse;
import merchant_manager.models.DejavooUser;
import merchant_manager.service.implementation.DejavooUserServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dejavoo-users")
@Tag(name = "Dejavoo Users", description = "Manage Dejavoo user accounts with encrypted credentials")
public class DejavooUserController {

    private final DejavooUserServiceImp dejavooUserService;

    public DejavooUserController(DejavooUserServiceImp dejavooUserService) {
        this.dejavooUserService = dejavooUserService;
    }

//    @PostMapping
//    @Operation(summary = "Create Dejavoo user", description = "Create a new Dejavoo user account (password will be AES encrypted)")
//    public ResponseEntity<DejavooUserResponse> createDejavooUser(@RequestBody DejavooUser dejavooUser) {
//        DejavooUser saved = dejavooUserService.save(dejavooUser);
//        return ResponseEntity.ok(dejavooUserService.getDejavooUserResponseById(saved.getId()));
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update Dejavoo user", description = "Update an existing Dejavoo user account")
//    public ResponseEntity<DejavooUserResponse> updateDejavooUser(@PathVariable Long id, @RequestBody DejavooUser dejavooUser) {
//        dejavooUser.setId(id);
//        DejavooUser updated = dejavooUserService.save(dejavooUser);
//        return ResponseEntity.ok(dejavooUserService.getDejavooUserResponseById(updated.getId()));
//    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get user by merchant ID", description = "Retrieve Dejavoo user for a specific merchant with decoded password")
    public ResponseEntity<DejavooUser> getDejavooUserByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(dejavooUserService.getDejavooUserByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user", description = "Delete a Dejavoo user by ID")
    public ResponseEntity<Void> deleteDejavooUser(@PathVariable Long id) {
        dejavooUserService.deleteDejavooUser(id);
        return ResponseEntity.noContent().build();
    }
}
