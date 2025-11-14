package merchant_manager.dto;

import lombok.Data;

@Data
public class JoinMerchantMilesRequest {
    private Long merchantId;
    private Long pointingSystemId;
}
