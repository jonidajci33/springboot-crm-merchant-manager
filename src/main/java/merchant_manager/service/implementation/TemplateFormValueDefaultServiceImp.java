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
        return templateFormValueDefaultRepository.findByTemplateFormDefaultIdAndRecordId(templateFormId, recordId);
    }

    @Override
    public TemplateFormValueDefault save(TemplateFormValueDefault templateFormValue) {
        return templateFormValueDefaultRepository.save(templateFormValue);
    }
}
