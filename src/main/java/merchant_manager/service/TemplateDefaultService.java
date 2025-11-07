package merchant_manager.service;

import merchant_manager.models.TemplateDefault;

public interface TemplateDefaultService {

    TemplateDefault findByMenuId(Long menuId);

}
