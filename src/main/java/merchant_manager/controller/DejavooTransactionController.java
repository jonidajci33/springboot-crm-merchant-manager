package merchant_manager.controller;

import merchant_manager.service.implementation.DejavooTransactionServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dejavoo-transactions")
public class DejavooTransactionController {

    private final DejavooTransactionServiceImp dejavooTransactionService;

    public DejavooTransactionController(DejavooTransactionServiceImp dejavooTransactionService) {
        this.dejavooTransactionService = dejavooTransactionService;
    }

    /**
     * Get daily volume for a specific merchant
     */
    @GetMapping("/volume/{merchantId}")
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

    /**
     * Update MerchantMiles with daily volume for a specific merchant
     */
    @PostMapping("/update-miles/{merchantId}")
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

    /**
     * Process all merchants - fetch transactions and update MerchantMiles
     */
    @PostMapping("/process-all")
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
