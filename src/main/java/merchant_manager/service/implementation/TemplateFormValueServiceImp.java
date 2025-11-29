package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.repository.TemplateValueFormRepository;
import merchant_manager.service.TemplateFormValueService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class TemplateFormValueServiceImp implements TemplateFormValueService {

    private final TemplateValueFormRepository templateValueFormRepository;
    private final TemplateFormServiceImp templateFormServiceImp;
    private final TemplateFormDefaultServiceImp templateFormDefaultServiceImp;
    private final TemplateFormValueDefaultServiceImp templateFormValueDefaultServiceImp;
    private final LeadServiceImp leadServiceImp;
    private final UserServiceImp userServiceImp;
    private final ContactServiceImp contactServiceImp;
    private final MerchantServiceImp merchantServiceImp;

    public Long addValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests){
        try {
            Long currentRecordId = recordId;
            if (recordId == null) {
                switch ((int) menuId.longValue()) {
                    case 2: // Lead
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
                    case 3: // Contact
                        Contact contact = new Contact();
                        contact.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        contact.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        contact.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        contact.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                        contactServiceImp.save(contact);
                        currentRecordId = contact.getId();
                        break;
                    case 4: // Merchant
                        Merchant merchant = new Merchant();
                        merchant.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        merchant.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                        merchant.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        merchant.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                        merchantServiceImp.save(merchant);
                        currentRecordId = merchant.getId();
                        break;
                    default:
                        throw new CustomExceptions.CustomValidationException("Record creation not supported for menu ID: " + menuId + ". Please provide a recordId.");
                }
            }
            for (AddValueRequest addValueRequest : addValueRequests) {
                if(addValueRequest.getIsDefault()){
                    TemplateFormValueDefault templateFormValue;
                    try {
                        templateFormValue = templateFormValueDefaultServiceImp.findByTemplateFormIdAndRecordId(templateFormDefaultServiceImp.getByKey(addValueRequest.getKey()).getId(), currentRecordId);
                    } catch (CustomExceptions.ResourceNotFoundException e) {
                        templateFormValue = new TemplateFormValueDefault();
                        templateFormValue.setTemplateFormDefault(templateFormDefaultServiceImp.getByKey(addValueRequest.getKey()));
                        templateFormValue.setRecordId(currentRecordId);
                        templateFormValue.setUser(userServiceImp.getLoggedUser());
                        templateFormValue.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        templateFormValue.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    }
                    templateFormValue.setValue(addValueRequest.getValue());
                    templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                    templateFormValueDefaultServiceImp.save(templateFormValue);
                }else {
                    TemplateFormValue templateFormValue;
                    try {
                        templateFormValue = findByTemplateFormIdAndRecordId(templateFormServiceImp.findByKey(addValueRequest.getKey()).getId(), currentRecordId);
                    } catch (CustomExceptions.ResourceNotFoundException e) {
                        templateFormValue = new TemplateFormValue();
                        templateFormValue.setTemplateForm(templateFormServiceImp.findByKey(addValueRequest.getKey()));
                        templateFormValue.setRecordId(currentRecordId);
                        templateFormValue.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                        templateFormValue.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    }
                    templateFormValue.setValue(addValueRequest.getValue());
                    templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
                    save(templateFormValue);
                }
            }
            return currentRecordId;
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding value to field: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to add field to template: " + e.getMessage());
        }
    }

    public TemplateFormValue findByTemplateFormIdAndRecordId(String key, Long recordId) {
        TemplateForm templateForm = templateFormServiceImp.findByKey(key);
        return findByTemplateFormIdAndRecordId(templateForm.getId(), recordId);
    }

    public TemplateFormValue findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId) {
        return templateValueFormRepository.findByTemplateFormIdAndRecordId(templateFormId, recordId).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Template From not found for record ID: " + recordId));
    }

    public TemplateFormValue save(TemplateFormValue templateFormValue) {
        return templateValueFormRepository.save(templateFormValue);
    }

    public List<TemplateFormValue> findByMenuIdAndRecordId(Long menuId, Long recordId) {
        return templateValueFormRepository.findByMenuIdAndRecordId(menuId, recordId);
    }

    public void deleteByMenuIdAndRecordId(Long menuId, Long recordId) {
        templateValueFormRepository.deleteByMenuIdAndRecordI(menuId, recordId);
    }

    public void deleteRecord(Long menuId, Long recordId) {
        try {
            User user = userServiceImp.getLoggedUser();

            // Delete all TemplateFormValueDefault records for this menu and record (including user filter)
            templateFormValueDefaultServiceImp.deleteByMenuIdAndRecordIdAndUser(menuId, recordId, user);
            deleteByMenuIdAndRecordId(menuId, recordId);
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
