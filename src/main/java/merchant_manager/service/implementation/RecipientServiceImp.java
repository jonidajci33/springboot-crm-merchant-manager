package merchant_manager.service.implementation;

import merchant_manager.models.Recipient;
import merchant_manager.repository.RecipientRepository;
import merchant_manager.service.RecipientService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipientServiceImp implements RecipientService {

    private final RecipientRepository recipientRepository;

    public RecipientServiceImp(RecipientRepository recipientRepository) {
        this.recipientRepository = recipientRepository;
    }

    @Override
    public List<Recipient> saveAll(List<Recipient> recipients) {
        return recipientRepository.saveAll(recipients);
    }

    @Override
    public Recipient save(Recipient recipient) {
        return recipientRepository.save(recipient);
    }

    @Override
    public void deleteRecipient(Long id) {
        recipientRepository.deleteById(id);
    }
}
