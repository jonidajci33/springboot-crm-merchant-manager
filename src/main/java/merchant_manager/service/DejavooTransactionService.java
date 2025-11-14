package merchant_manager.service;

import java.math.BigDecimal;

public interface DejavooTransactionService {

    /**
     * Fetches current day transactions from Dejavoo API for a specific merchant
     * and calculates the total volume
     *
     * @param merchantId the merchant ID
     * @return the total volume for the current day
     */
    BigDecimal fetchAndCalculateDailyVolume(Long merchantId);

    /**
     * Fetches current day transactions and updates MerchantMiles points
     *
     * @param merchantId the merchant ID
     */
    void updateMerchantMilesWithDailyVolume(Long merchantId);

    /**
     * Processes all active merchants with Dejavoo credentials
     * Fetches transactions and updates MerchantMiles for each
     */
    void processAllMerchantTransactions();

}
