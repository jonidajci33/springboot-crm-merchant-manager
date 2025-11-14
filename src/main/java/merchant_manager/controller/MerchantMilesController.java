package merchant_manager.controller;

import merchant_manager.dto.JoinMerchantMilesRequest;
import merchant_manager.models.MerchantMiles;
import merchant_manager.service.implementation.MerchantMilesServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant-miles")
public class MerchantMilesController {

    private final MerchantMilesServiceImp merchantMilesService;

    public MerchantMilesController(MerchantMilesServiceImp merchantMilesService) {
        this.merchantMilesService = merchantMilesService;
    }

    @PostMapping("/join")
    public ResponseEntity<MerchantMiles> joinMerchantMiles(@RequestBody JoinMerchantMilesRequest request) {
        MerchantMiles saved = merchantMilesService.joinMerchantMiles(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantMiles> getMerchantMilesById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantMilesService.getMerchantMilesById(id));
    }

    @GetMapping
    public ResponseEntity<List<MerchantMiles>> getAllMerchantMiles() {
        return ResponseEntity.ok(merchantMilesService.getAllMerchantMiles());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantMiles(@PathVariable Long id) {
        merchantMilesService.deleteMerchantMiles(id);
        return ResponseEntity.noContent().build();
    }
}
