package merchant_manager.dto;

import lombok.Data;
import merchant_manager.models.enums.DetailsType;

@Data
public class ContactMerchantDetailsRequest {
    private Long recordId;
    private DetailsType type;
    private Long searchMenuId;
    private Long companyId;
}
