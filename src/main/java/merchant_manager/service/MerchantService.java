package merchant_manager.service;

import merchant_manager.models.Merchant;

public interface MerchantService {

    Merchant save(Merchant merchant);

    void deleteMerchant(Long id);

}
