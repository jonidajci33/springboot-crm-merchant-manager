package merchant_manager.service;

import merchant_manager.models.DejavooCredentials;

import java.util.List;

public interface DejavooCredentialsService {

    DejavooCredentials save(DejavooCredentials credentials);

    DejavooCredentials getDejavooCredentialsById(Long id);

    DejavooCredentials getDejavooCredentialsByMerchantId(Long merchantId);

    List<DejavooCredentials> getAllDejavooCredentials();

    void deleteDejavooCredentials(Long id);

}
