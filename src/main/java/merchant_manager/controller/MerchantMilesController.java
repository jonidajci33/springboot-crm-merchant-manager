package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.dto.JoinMerchantMilesRequest;
import merchant_manager.models.MerchantMiles;
import merchant_manager.service.implementation.MerchantMilesServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant-miles")
@Tag(name = "Merchant Miles", description = "Manage merchant miles enrollment, tracking, and points")
public class MerchantMilesController {

    private final MerchantMilesServiceImp merchantMilesService;

    public MerchantMilesController(MerchantMilesServiceImp merchantMilesService) {
        this.merchantMilesService = merchantMilesService;
    }

    @PostMapping("/join")
    @Operation(summary = "Join merchant miles program", description = "Enroll a merchant in the miles program with selected pointing system")
    public ResponseEntity<MerchantMiles> joinMerchantMiles(@RequestBody JoinMerchantMilesRequest request) {
        MerchantMiles saved = merchantMilesService.joinMerchantMiles(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get merchant miles by ID", description = "Retrieve merchant miles record by its ID")
    public ResponseEntity<MerchantMiles> getMerchantMilesById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantMilesService.getMerchantMilesById(id));
    }

    @GetMapping
    @Operation(summary = "Get all merchant miles", description = "Retrieve all merchant miles records")
    public ResponseEntity<List<MerchantMiles>> getAllMerchantMiles() {
        return ResponseEntity.ok(merchantMilesService.getAllMerchantMiles());
    }

    @GetMapping("/merchant/{merchantId}")
    @Operation(summary = "Get merchant miles by merchant ID", description = "Retrieve merchant miles record by merchant ID")
    public ResponseEntity<List<MerchantMiles>> getMerchantMilesByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantMilesService.getMerchantMilesByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete merchant miles", description = "Delete a merchant miles record by ID")
    public ResponseEntity<Void> deleteMerchantMiles(@PathVariable Long id) {
        merchantMilesService.deleteMerchantMiles(id);
        return ResponseEntity.noContent().build();
    }
}
