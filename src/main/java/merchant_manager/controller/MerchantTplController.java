package merchant_manager.controller;

import merchant_manager.dto.MerchantTplRequest;
import merchant_manager.models.MerchantTpl;
import merchant_manager.service.implementation.MerchantTplServiceImp;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/merchant-tpl")
public class MerchantTplController {

    private final MerchantTplServiceImp merchantTplService;

    public MerchantTplController(MerchantTplServiceImp merchantTplService) {
        this.merchantTplService = merchantTplService;
    }

    @PostMapping
    public ResponseEntity<List<MerchantTpl>> createMerchantTpls(@RequestBody MerchantTplRequest request) {
        List<MerchantTpl> saved = merchantTplService.createMerchantTpls(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MerchantTpl> getMerchantTplById(@PathVariable Long id) {
        return ResponseEntity.ok(merchantTplService.getMerchantTplById(id));
    }

    @GetMapping
    public ResponseEntity<List<MerchantTpl>> getAllMerchantTpls() {
        return ResponseEntity.ok(merchantTplService.getAllMerchantTpls());
    }

    @GetMapping("/merchant/{merchantId}")
    public ResponseEntity<List<MerchantTpl>> getMerchantTplsByMerchantId(@PathVariable Long merchantId) {
        return ResponseEntity.ok(merchantTplService.getMerchantTplsByMerchantId(merchantId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMerchantTpl(@PathVariable Long id) {
        merchantTplService.deleteMerchantTpl(id);
        return ResponseEntity.noContent().build();
    }
}
