package merchant_manager.service.implementation;

import merchant_manager.dto.DejavooTransaction;
import merchant_manager.dto.DejavooTransactionResponse;
import merchant_manager.models.DejavooCredentials;
import merchant_manager.models.MerchantMiles;
import merchant_manager.repository.DejavooCredentialsRepository;
import merchant_manager.repository.MerchantMilesRepository;
import merchant_manager.service.DejavooTransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class DejavooTransactionServiceImp implements DejavooTransactionService {

    private static final Logger logger = LoggerFactory.getLogger(DejavooTransactionServiceImp.class);

    private final DejavooCredentialsRepository dejavooCredentialsRepository;
    private final MerchantMilesRepository merchantMilesRepository;
    private final RestTemplate restTemplate;

    public DejavooTransactionServiceImp(DejavooCredentialsRepository dejavooCredentialsRepository,
                                        MerchantMilesRepository merchantMilesRepository,
                                        RestTemplate restTemplate) {
        this.dejavooCredentialsRepository = dejavooCredentialsRepository;
        this.merchantMilesRepository = merchantMilesRepository;
        this.restTemplate = restTemplate;
    }

    @Override
    public BigDecimal fetchAndCalculateDailyVolume(Long merchantId) {
        // Get Dejavoo credentials for the merchant
        DejavooCredentials credentials = dejavooCredentialsRepository.findByMerchantId(merchantId)
                .orElseThrow(() -> new RuntimeException("Dejavoo credentials not found for merchant: " + merchantId));

        if (!Boolean.TRUE.equals(credentials.getIsActive())) {
            throw new RuntimeException("Dejavoo credentials are not active for merchant: " + merchantId);
        }

        // Prepare API request
        String currentDate = LocalDate.now().format(DateTimeFormatter.ISO_DATE);
        String apiUrl = String.format("%s/transactions?registerId=%s&date=%s",
                credentials.getApiUrl(),
                credentials.getRegisterId(),
                currentDate);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + credentials.getAuthKey());

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            // Call Dejavoo API
            ResponseEntity<DejavooTransactionResponse> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    entity,
                    DejavooTransactionResponse.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return calculateVolume(response.getBody().getTransactions());
            } else {
                logger.error("Failed to fetch transactions for merchant: {}", merchantId);
                return BigDecimal.ZERO;
            }
        } catch (Exception e) {
            logger.error("Error fetching transactions from Dejavoo API for merchant: {}", merchantId, e);
            throw new RuntimeException("Failed to fetch transactions from Dejavoo API", e);
        }
    }

    @Override
    public void updateMerchantMilesWithDailyVolume(Long merchantId) {
        // Fetch daily volume
        BigDecimal dailyVolume = fetchAndCalculateDailyVolume(merchantId);

        // Find MerchantMiles for this merchant
        List<MerchantMiles> merchantMilesList = merchantMilesRepository.findByMerchantId(merchantId);

        if (merchantMilesList.isEmpty()) {
            logger.warn("No MerchantMiles found for merchant: {}", merchantId);
            return;
        }

        // Update points for each MerchantMiles record
        for (MerchantMiles merchantMiles : merchantMilesList) {
            // Convert volume to points (you can customize this logic)
            Long volume = dailyVolume.longValue();

            Long pointsToAdd = (long) (volume * merchantMiles.getPointingSystem().getBps());

            Long currentPoints = merchantMiles.getPoints() != null ? merchantMiles.getPoints() : 0L;
            merchantMiles.setPoints(currentPoints + pointsToAdd);

            merchantMilesRepository.save(merchantMiles);
            logger.info("Updated MerchantMiles ID: {} with {} points from daily volume: {}",
                    merchantMiles.getId(), pointsToAdd, dailyVolume);
        }
    }

    @Override
    public void processAllMerchantTransactions() {
        // Get all active Dejavoo credentials
        List<DejavooCredentials> allCredentials = dejavooCredentialsRepository.findAll().stream()
                .filter(cred -> Boolean.TRUE.equals(cred.getIsActive()))
                .toList();

        logger.info("Processing transactions for {} merchants", allCredentials.size());

        for (DejavooCredentials credentials : allCredentials) {
            try {
                updateMerchantMilesWithDailyVolume(credentials.getMerchant().getId());
            } catch (Exception e) {
                logger.error("Error processing merchant {}", credentials.getMerchant().getId(), e);
                // Continue with next merchant even if one fails
            }
        }
    }

    /**
     * Calculate total volume from transactions
     * Only count approved SALE transactions
     */
    private BigDecimal calculateVolume(List<DejavooTransaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return transactions.stream()
                .filter(t -> "SALE".equalsIgnoreCase(t.getTransactionType()))
                .filter(t -> "APPROVED".equalsIgnoreCase(t.getStatus()))
                .map(DejavooTransaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
