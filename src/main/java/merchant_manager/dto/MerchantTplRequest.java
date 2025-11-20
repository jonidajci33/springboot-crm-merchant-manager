package merchant_manager.dto;

import lombok.Data;
import merchant_manager.models.MerchantTpl;

import java.util.List;

@Data
public class MerchantTplRequest {
    private List<MerchantTpl> merchantTplList;
}
