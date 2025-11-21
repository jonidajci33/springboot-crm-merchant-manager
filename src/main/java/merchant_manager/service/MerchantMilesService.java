package merchant_manager.service;

import merchant_manager.dto.JoinMerchantMilesRequest;
import merchant_manager.models.MerchantMiles;

import java.util.List;

public interface MerchantMilesService {

    MerchantMiles joinMerchantMiles(JoinMerchantMilesRequest request);

    MerchantMiles getMerchantMilesById(Long id);

    List<MerchantMiles> getAllMerchantMiles();

    List<MerchantMiles> getMerchantMilesByMerchantId(Long merchantId);

    void deleteMerchantMiles(Long id);

}
