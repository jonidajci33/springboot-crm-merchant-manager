package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.*;
import merchant_manager.models.enums.Role;
import merchant_manager.repository.TemplateDefaultRepository;
import merchant_manager.repository.TemplateRepository;
import merchant_manager.service.TemplateService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TemplateServiceImp implements TemplateService {

    private final TemplateRepository templateRepository;
    private final MenuSeviceImp menuSeviceImp;
    private final TemplateDefaultRepository templateDefaultRepository;

    @Override
    public void addDefaultTemplateToUser(User user, Company company) {
        List<Menu> menus = menuSeviceImp.getMenus();
//        if (user.getRole().equals(Role.ROLE_USER)) {
//            for (Menu menu : menus) {
//                Template template = new Template(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), user.getUsername(), user.getUsername(), menu, user);
//                templateRepository.save(template);
//            }
//        } else {
            for (Menu menu : menus) {
                TemplateDefault template = new TemplateDefault(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), user.getUsername(), user.getUsername(), menu, user, company);
                templateDefaultRepository.save(template);
            }
//        }
    }

    public void addTemplateToUser(User user) {
        List<Menu> menus = menuSeviceImp.getMenus();
        if (user.getRole().equals(Role.ROLE_USER)) {
            for (Menu menu : menus) {
                Template template = new Template(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), user.getUsername(), user.getUsername(), menu, user);
                templateRepository.save(template);
            }
        }
    }

    @Override
    public Template findByUserIdAndMenuId(Long userId, Long menuId) {
        return templateRepository.findByUserIdAndMenuId(userId, menuId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                        "Template not found for user ID: " + userId + " and menu ID: " + menuId));
    }

}
