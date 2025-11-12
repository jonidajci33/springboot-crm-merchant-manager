package merchant_manager.dto;

import lombok.Data;

@Data
public class ContactMerchantRequest {
    private Long leadId;
    private Long merchantId;
}
