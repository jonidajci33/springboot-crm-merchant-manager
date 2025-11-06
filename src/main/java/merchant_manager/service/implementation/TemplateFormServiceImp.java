package merchant_manager.service.implementation;

import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.Template;
import merchant_manager.models.TemplateForm;
import merchant_manager.repository.TemplateFormRepository;
import merchant_manager.repository.TemplateRepository;
import merchant_manager.service.TemplateFormService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class TemplateFormServiceImp implements TemplateFormService {

    private final TemplateFormRepository templateFormRepository;
    private final TemplateServiceImp templateServiceImp;
    private final UserServiceImp userServiceImp;

    public TemplateFormServiceImp(TemplateFormRepository templateFormRepository, TemplateServiceImp templateServiceImp, UserServiceImp userServiceImp) {
        this.templateFormRepository = templateFormRepository;
        this.templateServiceImp = templateServiceImp;
        this.userServiceImp = userServiceImp;
    }

    @Override
    @Transactional
    public List<TemplateForm> addFieldToTemplate(Long menuId, List<TemplateForm> templateForm) {
        try {
            Long userId = userServiceImp.getLoggedUser().getId();
            List<TemplateForm> formList = new ArrayList<>();
            // Find the template by userId and menuId
            Template template = templateServiceImp.findByUserIdAndMenuId(userId, menuId);

            for (TemplateForm templateFormCurrent : templateForm) {
                // Set the template to the form
                templateFormCurrent.setTemplate(template);
                // Set audit fields
                templateFormCurrent.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                templateFormCurrent.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                templateFormCurrent.setCreatedBy(template.getUser().getUsername());
                templateFormCurrent.setLastUpdatedBy(template.getUser().getUsername());
                // Save and return
                formList.add(templateFormRepository.save(templateFormCurrent));
            }
            return formList;
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding field to template: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to add field to template: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void removeFieldFromTemplate(List<String> key) {
        try {
            for (String keyCurrent : key) {
                removeByKey(keyCurrent);
            }
            log.info("Removed field from template. Key: {}", key);

        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error removing field from template: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to remove field from template: " + e.getMessage());
        }
    }

    @Override
    public List<TemplateForm> getTemplateFields(Long menuId) {
        try {
            Long userId = userServiceImp.getLoggedUser().getId();
            // Find the template by userId and menuId
            Template template = templateServiceImp.findByUserIdAndMenuId(userId, menuId);

            // Get all fields for this template ordered by priority
            return templateFormRepository.findByTemplateId(template.getId());

        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error fetching template fields: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to fetch template fields: " + e.getMessage());
        }
    }

    public TemplateForm findByKey(String key) {
        return templateFormRepository.findByKey(key).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Template not found for user ID: " + key));
    }

    private void removeByKey(String key) {
        templateFormRepository.deleteByKey(key);
    }

}
