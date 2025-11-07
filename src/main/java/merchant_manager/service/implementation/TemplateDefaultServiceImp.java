package merchant_manager.service.implementation;

import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.models.TemplateDefault;
import merchant_manager.repository.TemplateDefaultRepository;
import merchant_manager.service.TemplateDefaultService;
import org.springframework.stereotype.Service;

@Service
public class TemplateDefaultServiceImp implements TemplateDefaultService {

    private final TemplateDefaultRepository templateDefaultRepository;

    public TemplateDefaultServiceImp(TemplateDefaultRepository templateDefaultRepository) {
        this.templateDefaultRepository = templateDefaultRepository;
    }

    @Override
    public TemplateDefault findByMenuId(Long menuId) {
        return templateDefaultRepository.findByMenuId(menuId)
                .orElseThrow(() -> new CustomExceptions.ResourceNotFoundException(
                        "Template Default not found for menu ID: " + menuId));
    }

}
