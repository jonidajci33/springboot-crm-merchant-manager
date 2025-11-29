package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.DTO.ConfigDTO;
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
    @Transactional
    public List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, Long companyId, List<TemplateFormDefault> templateForm) {
        try {
            User user = userServiceImp.getLoggedUser();

            log.info("User role: {}, User ID: {}, Company ID: {}", user.getRole(), user.getId(), companyId);

            // Check if user is super admin or admin - both can manage default fields
            if (!user.getRole().equals(Role.ROLE_SUPERUSER) && !user.getRole().equals(Role.ROLE_ADMIN)) {
                throw new CustomExceptions.CustomValidationException("User is not a member of this role. Current role: " + user.getRole());
            }

            // SUPERUSER can access any company
            // ADMIN needs to have the company in their companies list
            if (user.getRole().equals(Role.ROLE_ADMIN)) {
                // Reload user with companies to avoid lazy loading issues
                User managedUser = userServiceImp.findByIdWithCompanies(user.getId());

                // Check if user has any companies
                if (managedUser.getCompanies() == null || managedUser.getCompanies().isEmpty()) {
                    throw new CustomExceptions.UnauthorizedAccessException("User has no assigned companies");
                }

                // Check if user has access to this specific company
                boolean hasCompany = managedUser.getCompanies()
                        .stream()
                        .anyMatch(c -> c.getId().equals(companyId));
                if (!hasCompany) {
                    throw new CustomExceptions.UnauthorizedAccessException("This user does not have permission to access company ID: " + companyId);
                }
            }
            // SUPERUSER doesn't need company check - they can access any company
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
        return templateFormDefaultRepository.findByTemplateIdAndSearchContact(templateId, true).orElse(null);
    }

    @Override
    public TemplateFormDefault findByTemplateIdAndSearchMerchant(Long templateId, Boolean searchMerchant) {
        return templateFormDefaultRepository.findByTemplateIdAndSearchMerchant(templateId, true).orElse(null);
    }

    @Override
    public TemplateFormDefault findByTemplateIdAndSearchLead(Long templateId, Boolean searchLead) {
        return templateFormDefaultRepository.findByTemplateIdAndSearchLead(templateId, true).orElse(null);
    }

    public ConfigDTO getConfig(Long companyId) {
        final Long CONTACT_MENU_ID = 3L;
        final Long MERCHANT_MENU_ID = 4L;
        final Long LEAD_MENU_ID = 2L;

        TemplateDefault templateContact = templateServiceImp.findByMenuIdAndCompanyId(CONTACT_MENU_ID, companyId);
        TemplateDefault templateMerchant = templateServiceImp.findByMenuIdAndCompanyId(MERCHANT_MENU_ID, companyId);
        TemplateDefault templateLead = templateServiceImp.findByMenuIdAndCompanyId(LEAD_MENU_ID, companyId);

        TemplateFormDefault templateFormDefaultContact = findByTemplateIdAndSearchContact(templateContact.getId(), true);
        TemplateFormDefault templateFormDefaultMerchant = findByTemplateIdAndSearchMerchant(templateMerchant.getId(), true);
        TemplateFormDefault templateFormDefaultLead = findByTemplateIdAndSearchLead(templateLead.getId(), true);

        ConfigDTO configDTO = new ConfigDTO();
        configDTO.setContactSearch(templateFormDefaultContact != null ? templateFormDefaultContact.getKey() : null);
        configDTO.setMerchantSearch(templateFormDefaultMerchant != null ? templateFormDefaultMerchant.getKey() : null);
        configDTO.setLeadSearch(templateFormDefaultLead != null ? templateFormDefaultLead.getKey() : null);

        return configDTO;
    }
}
