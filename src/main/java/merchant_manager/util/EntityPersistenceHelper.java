package merchant_manager.util;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Helper utility to handle entity persistence with retry logic for unique constraint violations
 */
@Component
public class EntityPersistenceHelper {

    private static final int MAX_RETRY_ATTEMPTS = 5;

    /**
     * Attempts to save an entity with retry logic for handling duplicate key violations
     *
     * @param saveOperation The save operation to execute (e.g., repository.save())
     * @param entityResetter A function to reset the unique field before retry (e.g., setting key to null)
     * @param <T> The entity type
     * @return The saved entity
     * @throws DataIntegrityViolationException if all retry attempts fail
     */
    public <T> T saveWithRetry(Supplier<T> saveOperation, Runnable entityResetter) {
        int attempts = 0;
        DataIntegrityViolationException lastException = null;

        while (attempts < MAX_RETRY_ATTEMPTS) {
            try {
                return saveOperation.get();
            } catch (DataIntegrityViolationException e) {
                lastException = e;
                attempts++;

                if (e.getMessage() != null && e.getMessage().contains("key")) {
                    // Reset the unique field to trigger regeneration
                    entityResetter.run();

                    if (attempts < MAX_RETRY_ATTEMPTS) {
                        // Wait a bit before retry to avoid collision
                        try {
                            Thread.sleep(10 * attempts); // Exponential backoff
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException("Retry interrupted", ie);
                        }
                        continue;
                    }
                }
                throw e;
            }
        }

        throw lastException != null ? lastException :
            new DataIntegrityViolationException("Failed to save entity after " + MAX_RETRY_ATTEMPTS + " attempts");
    }
}
