package merchant_manager.service;

import merchant_manager.models.DTO.AddValueRequest;
import merchant_manager.models.TemplateFormValue;

import java.util.List;

public interface TemplateFormValueService {

    Long addValuesToForm(Long menuId, Long recordId, List<AddValueRequest> addValueRequests);

    TemplateFormValue findByTemplateFormIdAndRecordId(String key, Long recordId);

    TemplateFormValue findByTemplateFormIdAndRecordId(Long templateFormId, Long recordId);

    TemplateFormValue save(TemplateFormValue templateFormValue);

    List<TemplateFormValue> findByMenuIdAndRecordId(Long menuId, Long recordId);

}
