package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.TemplateFormDefaultRepository;
import merchant_manager.service.TemplateFormDefaultService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final TemplateDefaultServiceImp templateServiceImp;

    @Override
    public List<TemplateFormDefault> getColumnsByMenuId(Long menuId) {
        TemplateDefault template = templateServiceImp.findByMenuId(menuId);
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
                List<TemplateFormDefault> formList = new ArrayList<>();
                // Find the template by userId and menuId
                TemplateDefault template = templateServiceImp.findByMenuId(menuId);

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

    @Override
    @Transactional
    public void removeFieldFromTemplate(List<String> key) {
        try {
            User  user = userServiceImp.getLoggedUser();
            if(user.getRole().equals(Role.ROLE_SUPERUSER)) {
                for (String keyCurrent : key) {
                    removeByKey(keyCurrent);
                }
                log.info("Removed field from Default template. Key: {}", key);
            }else {
                throw new CustomExceptions.ResourceNotFoundException("User is not a member of this role (Admin)");
            }
        } catch (CustomExceptions.ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error removing field from template: {}", e.getMessage(), e);
            throw new CustomExceptions.CustomValidationException("Failed to remove field from template: " + e.getMessage());
        }
    }

    public TemplateFormDefault removeByKey(String key) {
        return templateFormDefaultRepository.removeByKey(key).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Key not found"));
    }
}
