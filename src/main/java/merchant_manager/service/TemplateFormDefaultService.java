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
    List<TemplateFormDefault> getColumnsByMenuIdAndCompanyId(Long menuId, Long companyId);

    /**
     * Get column definition by key
     *
     * @param key The column key
     * @return Column definition
     */
    TemplateFormDefault getByKey(String key);

    List<TemplateFormDefault> addFieldToDefaultTemplate(Long menuId, Long companyId, List<TemplateFormDefault> templateForm);

    void removeFieldFromTemplate(List<String> key);

    void removeByKey(String key);

    TemplateFormDefault findByTemplateIdAndSearchContact(Long templateId, Boolean searchCustomer);

    TemplateFormDefault findByTemplateIdAndSearchMerchant(Long templateId, Boolean searchMerchant);
}
