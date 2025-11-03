package merchant_manager.service.implementation;

import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.models.Lead;
import merchant_manager.models.Template;
import merchant_manager.models.TemplateForm;
import merchant_manager.models.TemplateFormValue;
import merchant_manager.repository.TemplateValueFormRepository;
import merchant_manager.service.TemplateFormValueService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TemplateFormValueServiceImp {

    private final TemplateValueFormRepository templateValueFormRepository;
    private final TemplateFormServiceImp templateFormServiceImp;
    private final LeadServiceImp leadServiceImp;
    private final UserServiceImp userServiceImp;

    public TemplateFormValueServiceImp(TemplateValueFormRepository templateValueFormRepository, TemplateFormServiceImp templateFormServiceImp, LeadServiceImp leadServiceImp, UserServiceImp userServiceImp) {
        this.templateValueFormRepository = templateValueFormRepository;
        this.templateFormServiceImp = templateFormServiceImp;
        this.leadServiceImp = leadServiceImp;
        this.userServiceImp = userServiceImp;
    }

    public void addValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests){
        if(recordId == null){
            Long currentRecordId = null;
            switch((int) menuId.longValue()) {
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
                case 5:
                case 6:
            }
            for(AddValueRequest addValueRequest : addValueRequests){
                TemplateFormValue templateFormValue = new TemplateFormValue();
                templateFormValue.setTemplateForm(templateFormServiceImp.findByKey(addValueRequest.getKey()));
                templateFormValue.setValue(addValueRequest.getValue());
                templateFormValue.setRecordId(currentRecordId);
                templateFormValue.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                templateFormValue.setCreatedBy(userServiceImp.getLoggedUser().getUsername());
                templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
            }
        }else{
            for(AddValueRequest addValueRequest : addValueRequests){
                TemplateFormValue templateFormValue;
                try {
                    templateFormValue = findByTemplateFormIdAndRecordId(templateFormServiceImp.findByKey(addValueRequest.getKey()).getId(), recordId);
                }catch(CustomExceptions.ResourceNotFoundException e){
                    templateFormValue = new TemplateFormValue();
                    templateFormValue.setTemplateForm(templateFormServiceImp.findByKey(addValueRequest.getKey()));
                    templateFormValue.setRecordId(recordId);
                }
                templateFormValue.setValue(addValueRequest.getValue());
                templateFormValue.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                templateFormValue.setLastUpdatedBy(userServiceImp.getLoggedUser().getUsername());
            }
        }
    }

    public TemplateFormValue findByTemplateFormIdAndRecordId(String key, Long recordId) {
        TemplateForm templateForm = templateFormServiceImp.findByKey(key);
        return findByTemplateFormIdAndRecordId(templateForm.getId(), recordId);
    }

    public TemplateFormValue findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId) {
        return templateValueFormRepository.findByTemplateFormIdAndRecordId(templateFormId, recordId).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Template From not found for record ID: " + recordId));
    }

}
