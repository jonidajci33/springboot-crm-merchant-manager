package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.dto.ContactMerchantRequest;
import merchant_manager.dto.ContactMerchantWithDetailsDTO;
import merchant_manager.models.Contact;
import merchant_manager.models.ContactMerchant;
import merchant_manager.models.Lead;
import merchant_manager.models.Merchant;
import merchant_manager.models.TemplateFormValueDefault;
import merchant_manager.repository.ContactMerchantRepository;
import merchant_manager.repository.ContactRepository;
import merchant_manager.repository.LeadRepository;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.repository.TemplateFormValueDefaultRepository;
import merchant_manager.service.ContactMerchantService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ContactMerchantServiceImp implements ContactMerchantService {

    private final ContactMerchantRepository contactMerchantRepository;
    private final ContactRepository contactRepository;
    private final MerchantRepository merchantRepository;
    private final TemplateFormValueDefaultRepository templateFormValueDefaultRepository;
    private final TemplateDefaultServiceImp templateServiceImp;

    @Override
    public ContactMerchant saveContactMerchant(ContactMerchant contactMerchant) {
        return contactMerchantRepository.save(contactMerchant);
    }

    @Override
    public List<ContactMerchant> createContactMerchant(List<ContactMerchantRequest> requests) {

        List<ContactMerchant> contactMerchants = new ArrayList<>();

        for (ContactMerchantRequest contactMerchantRequest : requests) {
            Contact contact = contactRepository.findById(contactMerchantRequest.getContactId())
                    .orElseThrow(() -> new RuntimeException("Contact not found with id: " + contactMerchantRequest.getContactId()));
            Merchant merchant = merchantRepository.findById(contactMerchantRequest.getMerchantId())
                    .orElseThrow(() -> new RuntimeException("Merchant not found with id: " + contactMerchantRequest.getMerchantId()));

            ContactMerchant contactMerchant = new ContactMerchant();
            contactMerchant.setContact(contact);
            contactMerchant.setMerchant(merchant);
            contactMerchants.add(contactMerchant);
        }


        return contactMerchantRepository.saveAll(contactMerchants);
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
    public List<ContactMerchant> getContactMerchantsByLeadId(Long contactId) {
        return contactMerchantRepository.findByContactId(contactId);
    }

    @Override
    public List<ContactMerchant> getContactMerchantsByMerchantId(Long merchantId) {
        return contactMerchantRepository.findByMerchantId(merchantId);
    }

}
