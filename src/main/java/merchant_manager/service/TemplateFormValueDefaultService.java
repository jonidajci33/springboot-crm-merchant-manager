package merchant_manager.service;

import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.models.TemplateFormValue;
import merchant_manager.models.TemplateFormValueDefault;

import java.util.List;

public interface TemplateFormValueDefaultService {

    void addDefaultValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests);

    TemplateFormValueDefault findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId);

    TemplateFormValueDefault save(TemplateFormValueDefault templateFormValue);

    List<TemplateFormValueDefault> findByMenuIdAndRecordId(Long menuId, Long recordId);

    void deleteRecord(Long menuId, Long recordId);
}
