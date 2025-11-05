package merchant_manager.service;

import merchant_manager.models.TemplateForm;

import java.util.List;

public interface TemplateFormService {

    List<TemplateForm> addFieldToTemplate(Long menuId, List<TemplateForm> templateForm);

    void removeFieldFromTemplate(List<String> keys);

    List<TemplateForm> getTemplateFields(Long userId, Long menuId);

}
