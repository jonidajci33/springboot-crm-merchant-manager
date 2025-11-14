package merchant_manager.scheduler;

import merchant_manager.service.implementation.DejavooTransactionServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler to automatically process Dejavoo transactions
 * To enable scheduling, add @EnableScheduling to your main application class
 */
@Component
public class DejavooTransactionScheduler {

    private static final Logger logger = LoggerFactory.getLogger(DejavooTransactionScheduler.class);

    private final DejavooTransactionServiceImp dejavooTransactionService;

    public DejavooTransactionScheduler(DejavooTransactionServiceImp dejavooTransactionService) {
        this.dejavooTransactionService = dejavooTransactionService;
    }

    /**
     * Runs daily at midnight to process all merchant transactions
     * Cron format: second minute hour day month weekday
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void processDailyTransactions() {
        logger.info("Starting scheduled daily transaction processing");
        try {
            dejavooTransactionService.processAllMerchantTransactions();
            logger.info("Completed scheduled daily transaction processing");
        } catch (Exception e) {
            logger.error("Error during scheduled transaction processing", e);
        }
    }

    /**
     * Optional: Run every hour to keep points updated throughout the day
     * Uncomment if you want hourly updates instead of daily
     */
    // @Scheduled(cron = "0 0 * * * ?")
    // public void processHourlyTransactions() {
    //     logger.info("Starting scheduled hourly transaction processing");
    //     try {
    //         dejavooTransactionService.processAllMerchantTransactions();
    //         logger.info("Completed scheduled hourly transaction processing");
    //     } catch (Exception e) {
    //         logger.error("Error during scheduled transaction processing", e);
    //     }
    // }
}
