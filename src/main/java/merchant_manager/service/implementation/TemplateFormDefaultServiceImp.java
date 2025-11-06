package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.Template;
import merchant_manager.models.TemplateForm;
import merchant_manager.models.TemplateFormDefault;
import merchant_manager.models.User;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.TemplateFormDefaultRepository;
import merchant_manager.service.TemplateFormDefaultService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class TemplateFormDefaultServiceImp implements TemplateFormDefaultService {

    private final TemplateFormDefaultRepository templateFormDefaultRepository;
    private final UserServiceImp userServiceImp;
    private final TemplateServiceImp templateServiceImp;

    @Override
    public List<TemplateFormDefault> getColumnsByMenuId(Long menuId) {
        Long userId = userServiceImp.getLoggedUser().getId();
        Template template = templateServiceImp.findByUserIdAndMenuId(userId, menuId);
        return templateFormDefaultRepository.findAllByTemplateId(template.getId());
    }

    @Override
    public TemplateFormDefault getByKey(String key) {
        return templateFormDefaultRepository.findByKey(key)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                        "Column not found with key: " + key));
    }

    @Override
    public List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, List<TemplateFormDefault> templateForm) {
        try {
            User user = userServiceImp.getLoggedUser();
            if(user.getRole().equals(Role.ROLE_SUPERUSER)){
                Long userId = user.getId();
                List<TemplateFormDefault> formList = new ArrayList<>();
                // Find the template by userId and menuId
                Template template = templateServiceImp.findByUserIdAndMenuId(userId, menuId);

                for (TemplateFormDefault templateFormCurrent : templateForm) {
                    // Set the template to the form
                    templateFormCurrent.setTemplate(template);
                    // Set audit fields
                    templateFormCurrent.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormCurrent.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    templateFormCurrent.setCreatedBy(template.getUser().getUsername());
                    templateFormCurrent.setLastUpdatedBy(template.getUser().getUsername());
                    // Save and return
                    formList.add(templateFormDefaultRepository.save(templateFormCurrent));
                }
                return formList;
            }else{
                throw new CustomExceptions.CustomValidationException("User is not a member of this role");
            }
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error adding field to template: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to add field to template: " + e.getMessage());
        }
    }
}
