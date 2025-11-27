package merchant_manager.service;

import merchant_manager.models.Company;
import merchant_manager.models.Template;
import merchant_manager.models.User;

import java.util.Optional;

public interface TemplateService {

    void addDefaultTemplateToUser(User user, Company company);
    void addTemplateToUser(User user);
    Template findByUserIdAndMenuId(Long userId, Long menuId);

}
