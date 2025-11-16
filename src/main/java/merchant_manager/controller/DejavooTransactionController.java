package merchant_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import merchant_manager.service.implementation.DejavooTransactionServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dejavoo-transactions")
@Tag(name = "Dejavoo", description = "Manage Dejavoo payment processor credentials and transaction integration")
public class DejavooTransactionController {

    private final DejavooTransactionServiceImp dejavooTransactionService;

    public DejavooTransactionController(DejavooTransactionServiceImp dejavooTransactionService) {
        this.dejavooTransactionService = dejavooTransactionService;
    }

    @GetMapping("/volume/{merchantId}")
    @Operation(summary = "Get daily transaction volume", description = "Fetch and calculate current day transaction volume for a merchant")
    public ResponseEntity<Map<String, Object>> getDailyVolume(@PathVariable Long merchantId) {
        try {
            BigDecimal volume = dejavooTransactionService.fetchAndCalculateDailyVolume(merchantId);
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("dailyVolume", volume);
            response.put("status", "success");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/update-miles/{merchantId}")
    @Operation(summary = "Update merchant miles", description = "Fetch daily volume and update merchant miles points for a specific merchant")
    public ResponseEntity<Map<String, Object>> updateMerchantMiles(@PathVariable Long merchantId) {
        try {
            dejavooTransactionService.updateMerchantMilesWithDailyVolume(merchantId);
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("status", "success");
            response.put("message", "MerchantMiles updated successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("merchantId", merchantId);
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/process-all")
    @Operation(summary = "Process all merchants", description = "Fetch transactions and update merchant miles for all active merchants")
    public ResponseEntity<Map<String, Object>> processAllMerchants() {
        try {
            dejavooTransactionService.processAllMerchantTransactions();
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("message", "All merchant transactions processed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
}
