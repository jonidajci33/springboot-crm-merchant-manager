package merchant_manager.service.implementation;

import merchant_manager.dto.JoinMerchantMilesRequest;
import merchant_manager.models.DejavooUser;
import merchant_manager.models.Merchant;
import merchant_manager.models.MerchantMiles;
import merchant_manager.models.PointingSystem;
import merchant_manager.models.User;
import merchant_manager.repository.MerchantMilesRepository;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.repository.PointingSystemRepository;
import merchant_manager.service.DejavooUserService;
import merchant_manager.service.MerchantMilesService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class MerchantMilesServiceImp implements MerchantMilesService {

    private final MerchantMilesRepository merchantMilesRepository;
    private final UserServiceImp userServiceImp;
    private final MerchantRepository merchantRepository;
    private final PointingSystemRepository pointingSystemRepository;
    private final DejavooUserService dejavooUserService;
    private final SecureRandom secureRandom;
    private static final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

    public MerchantMilesServiceImp(MerchantMilesRepository merchantMilesRepository, UserServiceImp userServiceImp,
                                   MerchantRepository merchantRepository,
                                   PointingSystemRepository pointingSystemRepository,
                                   DejavooUserService dejavooUserService) {
        this.merchantMilesRepository = merchantMilesRepository;
        this.userServiceImp = userServiceImp;
        this.merchantRepository = merchantRepository;
        this.pointingSystemRepository = pointingSystemRepository;
        this.dejavooUserService = dejavooUserService;
        this.secureRandom = new SecureRandom();
    }

    @Override
    public MerchantMiles joinMerchantMiles(JoinMerchantMilesRequest request) {

        User currentUser = userServiceImp.getLoggedUser();

        // Fetch merchant
        Merchant merchant = merchantRepository.findById(request.getMerchantId())
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + request.getMerchantId()));

        // Fetch pointing system
        PointingSystem pointingSystem = pointingSystemRepository.findById(request.getPointingSystemId())
                .orElseThrow(() -> new RuntimeException("PointingSystem not found with id: " + request.getPointingSystemId()));

        // Generate random 24-character username and password
        String randomUsername = generateRandomString(24);
        String randomPassword = generateRandomString(24);

        // Create DejavooUser
        DejavooUser dejavooUser = DejavooUser.builder()
                .username(randomUsername)
                .password(randomPassword)
                .merchant(merchant)
                .passwordText(randomPassword)
                .active(true)
                .build();

        // Save DejavooUser (password will be BCrypt encoded by the service)
        dejavooUserService.save(dejavooUser);

        // Create new MerchantMiles with 0 points
        MerchantMiles merchantMiles = new MerchantMiles();
        merchantMiles.setMerchant(merchant);
        merchantMiles.setPointingSystem(pointingSystem);
        merchantMiles.setUser(currentUser);
        merchantMiles.setPoints(0L);
        merchantMiles.setCreatedBy(userServiceImp.getLoggedUser().getUsername());

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

    /**
     * Generates a random alphanumeric string of specified length
     * @param length The length of the random string to generate
     * @return A random alphanumeric string
     */
    private String generateRandomString(int length) {
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int randomIndex = secureRandom.nextInt(ALPHANUMERIC.length());
            sb.append(ALPHANUMERIC.charAt(randomIndex));
        }
        return sb.toString();
    }
}
