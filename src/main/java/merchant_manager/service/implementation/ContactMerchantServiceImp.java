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
import merchant_manager.models.TemplateDefault;
import merchant_manager.models.enums.DetailsType;
import merchant_manager.repository.ContactMerchantRepository;
import merchant_manager.repository.ContactRepository;
import merchant_manager.repository.LeadRepository;
import merchant_manager.repository.MerchantRepository;
import merchant_manager.repository.TemplateDefaultRepository;
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
    private final TemplateDefaultRepository templateDefaultRepository;
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

    public List<ContactMerchantWithDetailsDTO> getContactMerchantWithDetailsDTOById(ContactMerchantDetailsRequest request) {
        // 1. Get the TemplateDefault by menuId and companyId
        TemplateDefault template = templateDefaultRepository.findByMenuIdAndCompanyId(
                request.getSearchMenuId(),
                request.getCompanyId()
        ).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                "Template not found for menuId: " + request.getSearchMenuId() + " and companyId: " + request.getCompanyId()
        ));

        // 2. Get ContactMerchant relationships and extract IDs based on type
        List<Long> recordIds;
        List<ContactMerchant> relationships;

        if (request.getType() == DetailsType.MERCHANT) {
            // Get all contacts connected to this merchant
            relationships = contactMerchantRepository.findByMerchantId(request.getRecordId());
            recordIds = relationships.stream()
                    .map(rel -> rel.getContact().getId())
                    .collect(Collectors.toList());
        } else { // DetailsType.CONTACT
            // Get all merchants connected to this contact
            relationships = contactMerchantRepository.findByContactId(request.getRecordId());
            recordIds = relationships.stream()
                    .map(rel -> rel.getMerchant().getId())
                    .collect(Collectors.toList());
        }

        // 3. If no relationships found, return empty list
        if (recordIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 4. Get TemplateFormValueDefault based on type
        List<TemplateFormValueDefault> values;

        if (request.getType() == DetailsType.MERCHANT) {
            // Get values where searchContact = true
            values = templateFormValueDefaultRepository.findByTemplateIdAndSearchContactTrueAndRecordIds(
                    template.getId(),
                    recordIds
            );
        } else { // DetailsType.CONTACT
            // Get values where searchMerchant = true
            values = templateFormValueDefaultRepository.findByTemplateIdAndSearchMerchantTrueAndRecordIds(
                    template.getId(),
                    recordIds
            );
        }

        // 5. Transform to DTO - return list of contactId/merchantId and their values
        Map<Long, String> recordValueMap = values.stream()
                .collect(Collectors.toMap(
                        TemplateFormValueDefault::getRecordId,
                        TemplateFormValueDefault::getValue,
                        (v1, v2) -> v1 + ", " + v2 // Concatenate if multiple values for same record
                ));

        // 6. Create DTOs
        return recordIds.stream()
                .map(recordId -> new ContactMerchantWithDetailsDTO(
                        recordId,
                        recordValueMap.getOrDefault(recordId, "")
                ))
                .collect(Collectors.toList());
    }



//    public List<ContactMerchantWithDetailsDTO> getContactMerchantsWithDetails(Long contactId) {
//        List<ContactMerchant> relationships = contactMerchantRepository.findByContactId(contactId);
//
//        if (relationships.isEmpty()) {
//            return Collections.emptyList();
//        }
//
//        List<Long> merchantIds = relationships.stream()
//                .map(rel -> rel.getMerchant().getId())
//                .collect(Collectors.toList());
//
//        Long merchantMenuId = 6L;
//        var template = templateServiceImp.findByMenuId(merchantMenuId);
//
//        List<TemplateFormValueDefault> values = templateFormValueDefaultRepository
//                .findByTemplateIdAndRecordIds(template.getId(), merchantIds);
//
//        Map<Long, Map<String, String>> merchantFieldsMap = values.stream()
//                .collect(Collectors.groupingBy(
//                        TemplateFormValueDefault::getRecordId,
//                        Collectors.toMap(
//                                v -> v.getTemplateFormDefault().getKey(),
//                                TemplateFormValueDefault::getValue,
//                                (v1, v2) -> v1 // In case of duplicates, keep first
//                        )
//                ));
//
//        // 6. Transform to DTOs
//        return relationships.stream()
//                .map(rel -> {
//                    Long merchantId = rel.getMerchant().getId();
//                    Map<String, String> merchantFields = merchantFieldsMap.getOrDefault(
//                            merchantId,
//                            Collections.emptyMap()
//                    );
//
//                    return new ContactMerchantWithDetailsDTO(
//                            rel.getId(),
//                            rel.getContact().getId(),
//                            merchantId,
//                            merchantFields
//                    );
//                })
//                .collect(Collectors.toList());
//    }
}
