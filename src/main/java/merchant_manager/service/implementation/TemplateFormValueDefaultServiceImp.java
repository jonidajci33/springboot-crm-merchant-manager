package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.TemplateFormValueDefaultRepository;
import merchant_manager.repository.TemplateValueFormRepository;
import merchant_manager.service.TemplateFormValueDefaultService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TemplateFormValueDefaultServiceImp implements TemplateFormValueDefaultService {

    private final TemplateFormValueDefaultRepository templateFormValueDefaultRepository;
    private final TemplateFormDefaultServiceImp templateFormDefaultServiceImp;
    private final TemplateFormValueServiceImp  templateFormValueServiceImp;
    private final LeadServiceImp leadServiceImp;
    private final UserServiceImp userServiceImp;
    private final ContactServiceImp contactServiceImp;
    private final MerchantServiceImp merchantServiceImp;

    @Override
    public void addDefaultValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests) {
        try {
            if (recordId == null) {
                Long currentRecordId = null;
                switch ((int) menuId.longValue()) {
                    case 4:
                        Lead lead = new Lead();
                        lead.setIsSigned(false);
                        lead.setIsActive(true);
                        lead.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        lead.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        lead.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        lead.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                        lead = leadServiceImp.saveLead(lead);
                        currentRecordId = lead.getId();
                        break;
                    case 5:
                        Contact contact = new Contact();
                        contact.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        contact.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        contact.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        contact.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                        contactServiceImp.save(contact);
                        currentRecordId = contact.getId();
                        break;
                    case 6:
                        Merchant merchant = new Merchant();
                        merchant.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        merchant.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        merchant.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        merchant.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                        merchantServiceImp.save(merchant);
                        currentRecordId = merchant.getId();
                        break;
                }
                for (AddValueRequest addValueRequest : addValueRequests) {
                    TemplateFormValueDefault templateFormValue = new TemplateFormValueDefault();
                    templateFormValue.setTemplateFormDefault(templateFormDefaultServiceImp.getByKey(addValueRequest.getKey()));
                    templateFormValue.setValue(addValueRequest.getValue());
                    templateFormValue.setRecordId(currentRecordId);
                    templateFormValue.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormValue.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                    templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                    save(templateFormValue);
                }
            } else {
                for (AddValueRequest addValueRequest : addValueRequests) {
                    TemplateFormValueDefault templateFormValue;
                    try {
                        templateFormValue = findByTemplateFormIdAndRecordId(templateFormDefaultServiceImp.getByKey(addValueRequest.getKey()).getId(), recordId);
                    } catch (CustomExceptions.ResourceNotFoundException e) {
                        templateFormValue = new TemplateFormValueDefault();
                        templateFormValue.setUser(userServiceImp.getLoggedUser());
                        templateFormValue.setTemplateFormDefault(templateFormDefaultServiceImp.getByKey(addValueRequest.getKey()));
                        templateFormValue.setRecordId(recordId);
                        templateFormValue.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        templateFormValue.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    }
                    templateFormValue.setValue(addValueRequest.getValue());
                    templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                    save(templateFormValue);
                }
            }
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding value to field: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to add field to template: " + e.getMessage());
        }
    }


    @Override
    public TemplateFormValueDefault findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId) {
        TemplateFormValueDefault templateFormValueDefault = templateFormValueDefaultRepository.findByTemplateFormDefaultIdAndRecordId(templateFormId, recordId);
        if(templateFormValueDefault != null){
            return templateFormValueDefault;
        }else {
            throw new CustomExceptions.ResourceNotFoundException("Template form id not found: " + templateFormId + "and record id not found: " + recordId);
        }
    }

    @Override
    public TemplateFormValueDefault save(TemplateFormValueDefault templateFormValue) {
        return templateFormValueDefaultRepository.save(templateFormValue);
    }

    @Override
    public List<TemplateFormValueDefault> findByMenuIdAndRecordId(Long menuId, Long recordId) {
        return templateFormValueDefaultRepository.findByMenuIdAndRecordId(menuId, recordId);
    }

    @Override
    public void deleteRecord(Long menuId, Long recordId) {
        try {
            User user = userServiceImp.getLoggedUser();

            // Delete all TemplateFormValueDefault records for this menu and record (including user filter)
            templateFormValueDefaultRepository.deleteByMenuIdAndRecordIdAndUser(menuId, recordId, user);
            templateFormValueServiceImp.deleteByMenuIdAndRecordId(menuId, recordId);
            // Delete the actual record from Lead, Contact, or Merchant table
            switch (Math.toIntExact(menuId)) {
                case 4: // Lead
                    leadServiceImp.deleteLead(recordId);
                    log.info("Deleted Lead record with ID: {} by user: {}", recordId, user.getUsername());
                    break;
                case 5: // Contact
                    contactServiceImp.deleteContact(recordId);
                    log.info("Deleted Contact record with ID: {} by user: {}", recordId, user.getUsername());
                    break;
                case 6: // Merchant
                    merchantServiceImp.deleteMerchant(recordId);
                    log.info("Deleted Merchant record with ID: {} by user: {}", recordId, user.getUsername());
                    break;
                default:
                    throw new CustomExceptions.CustomValidationException("Invalid menuId: " + menuId);
            }

            log.info("Successfully deleted record with ID: {} from menu: {} by user: {}", recordId, menuId, user.getUsername());
        } catch (CustomExceptions.ResourceNotFoundException | CustomExceptions.CustomValidationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error deleting record: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to delete record: " + e.getMessage());
        }
    }
}
