package merchant_manager.service;

import merchant_manager.dto.MerchantTplRequest;
import merchant_manager.models.MerchantTpl;

import java.util.List;

public interface MerchantTplService {
    List<MerchantTpl> createMerchantTpls(MerchantTplRequest request);
    MerchantTpl getMerchantTplById(Long id);
    List<MerchantTpl> getMerchantTplsByMerchantId(Long merchantId);
    List<MerchantTpl> getAllMerchantTpls();
    void deleteMerchantTpl(Long id);
}
