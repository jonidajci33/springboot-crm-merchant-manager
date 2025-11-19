package merchant_manager.service.implementation;

import lombok.AllArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.EsignTemplate;
import merchant_manager.models.FileMetadata;
import merchant_manager.models.User;
import merchant_manager.repository.EsignTemplateRepository;
import merchant_manager.service.EsignTemplateService;
import merchant_manager.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Service
@AllArgsConstructor
public class EsignTemplateServiceImp implements EsignTemplateService {

    private final EsignTemplateRepository esignTemplateRepository;
    private final SupabaseStorageService supabaseStorageService;
    private final CloudStorageServiceImp cloudStorageService;
    private final UserServiceImp userServiceImp;

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
        User user = userServiceImp.getLoggedUser();
        if(esignTemplate.getFileMetadata() != null) {
            if (esignTemplate.getFileMetadata().getId() != null) {
                FileMetadata fileMetadata = cloudStorageService.getFileMetadata(esignTemplate.getFileMetadata().getId());
                if (fileMetadata.getOriginalFilename().equals(file.getOriginalFilename())) {
                    esignTemplate.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
                    esignTemplate.setLastUpdatedBy(user.getUsername());
                    savedEsignTemplate = save(esignTemplate);
                }
            }
        } else {
            FileMetadata newFile = cloudStorageService.uploadFile(file);
            esignTemplate.setFileMetadata(newFile);
            esignTemplate.setCreatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            esignTemplate.setCreatedBy(user.getUsername());
            esignTemplate.setLastUpdatedBy(user.getUsername());
            esignTemplate.setUpdatedAt(ZonedDateTime.now(ZoneId.of("America/New_York")).toLocalDateTime());
            savedEsignTemplate = save(esignTemplate);
        }
        return savedEsignTemplate;
    }


}
