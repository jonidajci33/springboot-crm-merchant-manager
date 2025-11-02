package merchant_manager.service.implementation;

import merchant_manager.models.Menu;
import merchant_manager.models.Template;
import merchant_manager.models.User;
import merchant_manager.repository.TemplateRepository;
import merchant_manager.service.TemplateService;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class TemplateServiceImp implements TemplateService {

    private final TemplateRepository templateRepository;
    private final MenuSeviceImp menuSeviceImp;

    public TemplateServiceImp(TemplateRepository templateRepository, MenuSeviceImp menuSeviceImp) {
        this.templateRepository = templateRepository;
        this.menuSeviceImp = menuSeviceImp;
    }

    @Override
    public void addDefaultTemplateToUser(User user) {
        List<Menu> menus = menuSeviceImp.getMenus();
        for (Menu menu : menus) {
            Template template = new Template(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime(), user.getUsername(), user.getUsername(), menu, user);
            templateRepository.save(template);
        }
    }

    @Override
    public void updateTemplate(Template template) {

    }
}
