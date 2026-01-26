package merchant_manager.service;

import merchant_manager.models.Recipient;

import java.util.List;

public interface RecipientService {

    List<Recipient> saveAll(List<Recipient> recipients);

    Recipient save(Recipient recipient);

    void deleteRecipient(Long id);
}
