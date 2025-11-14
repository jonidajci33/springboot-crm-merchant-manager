package merchant_manager.service.implementation;

import merchant_manager.dto.JoinMerchantMilesRequest;
import merchant_manager.models.Merchant;
import merchant_manager.models.MerchantMiles;
import merchant_manager.models.PointingSystem;
import merchant_manager.models.User;
import merchant_manager.repository.MerchantMilesRepository;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.repository.PointingSystemRepository;
import merchant_manager.service.MerchantMilesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MerchantMilesServiceImp implements MerchantMilesService {

    private final MerchantMilesRepository merchantMilesRepository;
    private final MerchantRepository merchantRepository;
    private final PointingSystemRepository pointingSystemRepository;

    public MerchantMilesServiceImp(MerchantMilesRepository merchantMilesRepository,
                                   MerchantRepository merchantRepository,
                                   PointingSystemRepository pointingSystemRepository) {
        this.merchantMilesRepository = merchantMilesRepository;
        this.merchantRepository = merchantRepository;
        this.pointingSystemRepository = pointingSystemRepository;
    }

    @Override
    public MerchantMiles joinMerchantMiles(JoinMerchantMilesRequest request) {
        // Get current logged in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) authentication.getPrincipal();

        // Fetch merchant
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + request.getMerchantId()));

        // Fetch pointing system
        PointingSystem pointingSystem = pointingSystemRepository.findById(request.getPointingSystemId())
                .orElseThrow(() -> new RuntimeException("PointingSystem not found with id: " + request.getPointingSystemId()));

        // Create new MerchantMiles with 0 points
        MerchantMiles merchantMiles = new MerchantMiles();
        merchantMiles.setMerchant(merchant);
        merchantMiles.setPointingSystem(pointingSystem);
        merchantMiles.setUser(currentUser);
        merchantMiles.setPoints(0L);

        // Save and return
        return merchantMilesRepository.save(merchantMiles);
    }

    @Override
    public MerchantMiles getMerchantMilesById(Long id) {
        return merchantMilesRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("MerchantMiles not found with id: " + id));
    }

    @Override
    public List<MerchantMiles> getAllMerchantMiles() {
        return merchantMilesRepository.findAll();
    }

    @Override
    public void deleteMerchantMiles(Long id) {
        merchantMilesRepository.deleteById(id);
    }
}
