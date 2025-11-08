package merchant_manager.models.DTO;

import lombok.Data;

@Data
public class AddValueRequest {

    private String key;
    private String value;
    private Boolean isDefault;

}
