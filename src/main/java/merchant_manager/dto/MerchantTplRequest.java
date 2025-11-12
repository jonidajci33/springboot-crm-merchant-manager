package merchant_manager.dto;

import lombok.Data;

import java.util.List;

@Data
public class MerchantTplRequest {
    private Long merchantId;
    private List<String> tpls;
}
