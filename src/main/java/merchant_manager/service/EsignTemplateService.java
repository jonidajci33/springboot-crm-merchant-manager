package merchant_manager.service;

import merchant_manager.models.EsignTemplate;
import org.springframework.web.multipart.MultipartFile;

public interface EsignTemplateService {

    EsignTemplate save(EsignTemplate esignTemplate);
    EsignTemplate findById(Long id);
    EsignTemplate createTemplate(EsignTemplate esignTemplate, MultipartFile file);
}
