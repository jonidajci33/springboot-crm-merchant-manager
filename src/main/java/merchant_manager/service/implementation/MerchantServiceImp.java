package merchant_manager.service.implementation;

import merchant_manager.models.Merchant;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.service.MerchantService;
import org.springframework.stereotype.Service;

@Service
public class MerchantServiceImp implements MerchantService {

    private final MerchantRepository merchantRepository;

    public MerchantServiceImp(MerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    @Override
    public Merchant save(Merchant merchant) {
        return merchantRepository.save(merchant);
    }
}
