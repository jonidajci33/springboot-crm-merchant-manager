package merchant_manager.service.implementation;

import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.dto.DejavooUserResponse;
import merchant_manager.models.DejavooUser;
import merchant_manager.repository.DejavooUserRepository;
import merchant_manager.service.DejavooUserService;
import merchant_manager.util.PasswordEncryptionUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DejavooUserServiceImp implements DejavooUserService {

    private final DejavooUserRepository dejavooUserRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public DejavooUserServiceImp(DejavooUserRepository dejavooUserRepository, BCryptPasswordEncoder passwordEncoder) {
        this.dejavooUserRepository = dejavooUserRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public DejavooUser save(DejavooUser dejavooUser) {
        // Encrypt password using AES (reversible encryption) if it's plain text
        if (dejavooUser.getPassword() != null) {
            dejavooUser.setPassword(passwordEncoder.encode(dejavooUser.getPassword()));
        }
        return dejavooUserRepository.save(dejavooUser);
    }

    @Override
    public DejavooUser getDejavooUserByMerchantId(Long merchantId) {
        return dejavooUserRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("DejavooUser not found for merchant id: " + merchantId));
    }

    @Override
    public void deleteDejavooUser(Long id) {
        dejavooUserRepository.deleteById(id);
    }
}
