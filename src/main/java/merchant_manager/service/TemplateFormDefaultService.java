package merchant_manager.service;

import merchant_manager.models.TemplateForm;
import merchant_manager.models.TemplateFormDefault;

import java.util.List;

public interface TemplateFormDefaultService {

    /**
     * Get all column definitions for a template
     *
     * @param menuId The Menu ID
     * @return List of column definitions ordered by priority
     */
    List<TemplateFormDefault> getColumnsByMenuId(Long menuId);

    /**
     * Get column definition by key
     *
     * @param key The column key
     * @return Column definition
     */
    TemplateFormDefault getByKey(String key);

    List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, List<TemplateFormDefault> templateForm);
}
