package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.DTO.ConfigDTO;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.CompanyRepository;
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
    private final CompanyRepository companyRepository;

    @Override
    public List<TemplateFormDefault> getColumnsByMenuIdAndCompanyId(Long menuId, Long companyId) {
        TemplateDefault template = templateServiceImp.findByMenuIdAndCompanyId(menuId, companyId);
        return templateFormDefaultRepository.findByTemplateId(template.getId());
    }

    @Override
    public TemplateFormDefault getByKey(String key) {
        return templateFormDefaultRepository.findByKey(key)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                        "Column not found with key: " + key));
    }

    @Override
    public List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, Long companyId, List<TemplateFormDefault> templateForm) {
        try {
            User user = userServiceImp.getLoggedUser();
            boolean hasCompany = companyRepository.existsByIdAndUserId(companyId, user.getId());
            if (!hasCompany) {
                throw new CustomExceptions.UnauthorizedAccessException("This user does not have permission to view dynamic records");
            }
            if(user.getRole().equals(Role.ROLE_SUPERUSER)){
                List<TemplateFormDefault> formList = new ArrayList<>();
                // Find the template by userId and menuId
                TemplateDefault template = templateServiceImp.findByMenuIdAndCompanyId(menuId, companyId);

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

    public void removeByKey(String key) {
        templateFormDefaultRepository.deleteByKey(key);
    }

    @Override
    public TemplateFormDefault findByTemplateIdAndSearchContact(Long templateId, Boolean searchCustomer) {
        return templateFormDefaultRepository.findByTemplateIdAndSearchContact(templateId, true).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Key for Search Customer not found with key: " + templateId));
    }

    @Override
    public TemplateFormDefault findByTemplateIdAndSearchMerchant(Long templateId, Boolean searchMerchant) {
        return templateFormDefaultRepository.findByTemplateIdAndSearchMerchant(templateId, true).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Key for Search Merchant not found with key: " + templateId));
    }

    @Override
    public TemplateFormDefault findByTemplateIdAndSearchLead(Long templateId, Boolean searchLead) {
        return templateFormDefaultRepository.findByTemplateIdAndSearchLead(templateId, true).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("Key for Search Lead not found with key: " + templateId));
    }

    public ConfigDTO getConfig(Long companyId) {
        TemplateDefault templateLead = templateServiceImp.findByMenuIdAndCompanyId(4L, companyId);
        TemplateDefault templateCustomer = templateServiceImp.findByMenuIdAndCompanyId(5L, companyId);
        TemplateDefault templateMerchant = templateServiceImp.findByMenuIdAndCompanyId(6L , companyId);

        TemplateFormDefault templateFormDefaultLead = findByTemplateIdAndSearchLead(templateLead.getId(), true);
        TemplateFormDefault templateFormDefaultCustomer = findByTemplateIdAndSearchContact(templateCustomer.getId(), true);
        TemplateFormDefault templateFormDefaultMerchant = findByTemplateIdAndSearchMerchant(templateMerchant.getId(), true);

        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setLeadSearch(templateFormDefaultLead.getKey());
        configDTO.setContactSearch(templateFormDefaultCustomer.getKey());
        configDTO.setMerchantSearch(templateFormDefaultMerchant.getKey());

        return configDTO;
    }
}
