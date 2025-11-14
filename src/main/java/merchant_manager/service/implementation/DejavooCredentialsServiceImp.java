package merchant_manager.service.implementation;

import merchant_manager.models.DejavooCredentials;
import merchant_manager.repository.DejavooCredentialsRepository;
import merchant_manager.service.DejavooCredentialsService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DejavooCredentialsServiceImp implements DejavooCredentialsService {

    private final DejavooCredentialsRepository dejavooCredentialsRepository;

    public DejavooCredentialsServiceImp(DejavooCredentialsRepository dejavooCredentialsRepository) {
        this.dejavooCredentialsRepository = dejavooCredentialsRepository;
    }

    @Override
    public DejavooCredentials save(DejavooCredentials credentials) {
        return dejavooCredentialsRepository.save(credentials);
    }

    @Override
    public DejavooCredentials getDejavooCredentialsById(Long id) {
        return dejavooCredentialsRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("DejavooCredentials not found with id: " + id));
    }

    @Override
    public DejavooCredentials getDejavooCredentialsByMerchantId(Long merchantId) {
        return dejavooCredentialsRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("DejavooCredentials not found for merchant id: " + merchantId));
    }

    @Override
    public List<DejavooCredentials> getAllDejavooCredentials() {
        return dejavooCredentialsRepository.findAll();
    }

    @Override
    public void deleteDejavooCredentials(Long id) {
        dejavooCredentialsRepository.deleteById(id);
    }
}
