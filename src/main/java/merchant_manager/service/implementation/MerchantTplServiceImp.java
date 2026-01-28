package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.dto.MerchantTplRequest;
import merchant_manager.models.Merchant;
import merchant_manager.models.MerchantTpl;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.repository.MerchantTplRepository;
import merchant_manager.service.MerchantTplService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class MerchantTplServiceImp implements MerchantTplService {

    private final MerchantTplRepository merchantTplRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public List<MerchantTpl> createMerchantTpls(MerchantTplRequest request) {

        List<MerchantTpl> merchantTpls = request.getMerchantTplList();

        return merchantTplRepository.saveAll(merchantTpls);
    }

    @Override
    public MerchantTpl getMerchantTplById(Long id) {
        return merchantTplRepository.findById(id)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("MerchantTpl not found with id: " + id));
    }

    @Override
    public List<MerchantTpl> getMerchantTplsByMerchantId(Long merchantId) {
        return merchantTplRepository.findByMerchantId(merchantId);
    }

    @Override
    public List<MerchantTpl> getAllMerchantTpls() {
        return merchantTplRepository.findAll();
    }

    @Override
    public void deleteMerchantTpl(Long id) {
        merchantTplRepository.deleteById(id);
    }
}
