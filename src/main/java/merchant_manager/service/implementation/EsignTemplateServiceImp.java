package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.EsignTemplate;
import merchant_manager.models.FileMetadata;
import merchant_manager.repository.EsignTemplateRepository;
import merchant_manager.service.EsignTemplateService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@AllArgsConstructor
public class EsignTemplateServiceImp implements EsignTemplateService {

    private final EsignTemplateRepository esignTemplateRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final CloudStorageServiceImp cloudStorageService;

    @Override
    public EsignTemplate save(EsignTemplate esignTemplate) {
        return esignTemplateRepository.save(esignTemplate);
    }

    @Override
    public EsignTemplate findById(Long id) {
        return esignTemplateRepository.findById(id).orElseThrow(() -> new CustomExceptions.ResourceNotFoundException("EsignTemplate not found"));
    }



    @Override
    public EsignTemplate createTemplate(EsignTemplate esignTemplate, MultipartFile file) {
        EsignTemplate savedEsignTemplate = null;
        if(esignTemplate.getFileMetadata() != null) {
            if (esignTemplate.getFileMetadata().getId() != null) {
                FileMetadata fileMetadata = cloudStorageService.getFileMetadata(esignTemplate.getFileMetadata().getId());
                if (fileMetadata.getOriginalFilename().equals(file.getOriginalFilename())) {
                    savedEsignTemplate = save(esignTemplate);
                }
            }
        } else {
            FileMetadata newFile = cloudStorageService.uploadFile(file);
            esignTemplate.setFileMetadata(newFile);
            savedEsignTemplate = save(esignTemplate);
        }
        return savedEsignTemplate;
    }


}
