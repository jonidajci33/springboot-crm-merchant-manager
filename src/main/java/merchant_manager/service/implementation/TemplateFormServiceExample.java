package merchant_manager.service.implementation;

import merchant_manager.models.TemplateForm;
import merchant_manager.models.TemplateFormDefault;
import merchant_manager.repository.TemplateFormRepository;
import merchant_manager.util.EntityPersistenceHelper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

/**
 * Example service showing how to use EntityPersistenceHelper for saving entities with unique hash keys
 * This ensures that if a hash collision occurs, the entity will be retried with a new hash
 *
 * NOTE: This is an example implementation. Use this pattern in your actual service classes.
 * You'll need to create a TemplateFormDefaultRepository if it doesn't exist.
 */
@Service
public class TemplateFormServiceExample {

    private final TemplateFormRepository templateFormRepository;
    private final EntityPersistenceHelper persistenceHelper;
    // Inject TemplateFormDefaultRepository when you create it

    public TemplateFormServiceExample(TemplateFormRepository templateFormRepository,
                                     EntityPersistenceHelper persistenceHelper) {
        this.templateFormRepository = templateFormRepository;
        this.persistenceHelper = persistenceHelper;
    }

    /**
     * Saves a TemplateForm with automatic retry on duplicate key violations
     * The retry mechanism will:
     * 1. Attempt to save the entity
     * 2. If a duplicate key error occurs, reset the key to null
     * 3. The @PrePersist method will generate a new hash
     * 4. Retry up to 5 times with exponential backoff
     */
    public TemplateForm saveTemplateForm(TemplateForm templateForm) {
        return persistenceHelper.saveWithRetry(
            () -> templateFormRepository.save(templateForm),
            () -> templateForm.setKey(null) // Reset key to null to trigger regeneration
        );
    }

    /**
     * Example for TemplateFormDefault (same pattern)
     * Replace 'someRepository' with your actual TemplateFormDefaultRepository
     */
    public <T extends JpaRepository<TemplateFormDefault, Long>> TemplateFormDefault saveTemplateFormDefault(
            TemplateFormDefault templateFormDefault, T repository) {
        return persistenceHelper.saveWithRetry(
            () -> repository.save(templateFormDefault),
            () -> templateFormDefault.setKey(null)
        );
    }

    // Add other business logic methods here...
}
