package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.Contact;
import merchant_manager.models.ContactMerchant;
import merchant_manager.models.Lead;
import merchant_manager.models.Merchant;
import merchant_manager.repository.ContactMerchantRepository;
import merchant_manager.repository.ContactRepository;
import merchant_manager.repository.LeadRepository;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.service.ContactMerchantService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ContactMerchantServiceImp implements ContactMerchantService {

    private final ContactMerchantRepository contactMerchantRepository;
    private final ContactRepository contactRepository;
    private final MerchantRepository merchantRepository;

    @Override
    public ContactMerchant saveContactMerchant(ContactMerchant contactMerchant) {
        return contactMerchantRepository.save(contactMerchant);
    }

    @Override
    public ContactMerchant createContactMerchant(Long contactId, Long merchantId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new RuntimeException("Contact not found with id: " + contactId));
        Merchant merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + merchantId));

        ContactMerchant contactMerchant = new ContactMerchant();
        contactMerchant.setContact(contact);
        contactMerchant.setMerchant(merchant);

        return contactMerchantRepository.save(contactMerchant);
    }

    @Override
    public ContactMerchant getContactMerchantById(Long id) {
        return contactMerchantRepository.findById(id).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(""));
    }

    @Override
    public List<ContactMerchant> getAllContactMerchants() {
        return contactMerchantRepository.findAll();
    }

    @Override
    public void deleteContactMerchant(Long id) {
        contactMerchantRepository.deleteById(id);
    }

    @Override
    public List<ContactMerchant> getContactMerchantsByLeadId(Long leadId) {
        return contactMerchantRepository.findByLeadId(leadId);
    }

    @Override
    public List<ContactMerchant> getContactMerchantsByMerchantId(Long merchantId) {
        return contactMerchantRepository.findByMerchantId(merchantId);
    }
}
