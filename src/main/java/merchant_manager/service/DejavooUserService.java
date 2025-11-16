package merchant_manager.service;

import merchant_manager.dto.DejavooUserResponse;
import merchant_manager.models.DejavooUser;

import java.util.List;

public interface DejavooUserService {

    DejavooUser save(DejavooUser dejavooUser);

    DejavooUser getDejavooUserByMerchantId(Long merchantId);

    void deleteDejavooUser(Long id);

}
