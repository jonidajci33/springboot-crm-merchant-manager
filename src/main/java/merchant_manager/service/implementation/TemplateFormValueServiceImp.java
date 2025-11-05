package merchant_manager.service.implementation;

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
public class TemplateFormValueServiceImp {

    private final TemplateValueFormRepository templateValueFormRepository;
    private final TemplateFormServiceImp templateFormServiceImp;
    private final LeadServiceImp leadServiceImp;
    private final UserServiceImp userServiceImp;
    private final ContactServiceImp contactServiceImp;
    private final MerchantServiceImp merchantServiceImp;

    public TemplateFormValueServiceImp(TemplateValueFormRepository templateValueFormRepository, TemplateFormServiceImp templateFormServiceImp, LeadServiceImp leadServiceImp, UserServiceImp userServiceImp, ContactServiceImp contactServiceImp, MerchantServiceImp merchantServiceImp) {
        this.templateValueFormRepository = templateValueFormRepository;
        this.templateFormServiceImp = templateFormServiceImp;
        this.leadServiceImp = leadServiceImp;
        this.userServiceImp = userServiceImp;
        this.contactServiceImp = contactServiceImp;
        this.merchantServiceImp = merchantServiceImp;
    }

    public void addValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests){
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
                    TemplateFormValue templateFormValue = new TemplateFormValue();
                    templateFormValue.setTemplateForm(templateFormServiceImp.findByKey(addValueRequest.getKey()));
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
                    TemplateFormValue templateFormValue;
                    try {
                        templateFormValue = findByTemplateFormIdAndRecordId(templateFormServiceImp.findByKey(addValueRequest.getKey()).getId(), recordId);
                    } catch (CustomExceptions.ResourceNotFoundException e) {
                        templateFormValue = new TemplateFormValue();
                        templateFormValue.setTemplateForm(templateFormServiceImp.findByKey(addValueRequest.getKey()));
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

}
