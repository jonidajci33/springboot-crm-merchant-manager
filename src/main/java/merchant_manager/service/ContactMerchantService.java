package merchant_manager.service;

import merchant_manager.models.ContactMerchant;

import java.util.List;
import java.util.Optional;

public interface ContactMerchantService {
    ContactMerchant saveContactMerchant(ContactMerchant contactMerchant);
    ContactMerchant createContactMerchant(Long leadId, Long merchantId);
    ContactMerchant getContactMerchantById(Long id);
    List<ContactMerchant> getAllContactMerchants();
    void deleteContactMerchant(Long id);
    List<ContactMerchant> getContactMerchantsByLeadId(Long leadId);
    List<ContactMerchant> getContactMerchantsByMerchantId(Long merchantId);
}
