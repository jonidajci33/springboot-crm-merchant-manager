package merchant_manager.service;

import merchant_manager.dto.ContactMerchantRequest;
import merchant_manager.models.ContactMerchant;

import java.util.List;
import java.util.Optional;

public interface ContactMerchantService {
    ContactMerchant saveContactMerchant(ContactMerchant contactMerchant);
    List<ContactMerchant> createContactMerchant(List<ContactMerchantRequest> requests);
    ContactMerchant getContactMerchantById(Long id);
    List<ContactMerchant> getAllContactMerchants();
    void deleteContactMerchant(Long id);
    List<ContactMerchant> getContactMerchantsByLeadId(Long leadId);
    List<ContactMerchant> getContactMerchantsByMerchantId(Long merchantId);
}
