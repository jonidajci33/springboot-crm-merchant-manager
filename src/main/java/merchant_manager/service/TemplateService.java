package merchant_manager.service;

import merchant_manager.models.Template;
import merchant_manager.models.User;

public interface TemplateService {

    void addDefaultTemplateToUser(User user);

    void updateTemplate(Template template);

}
