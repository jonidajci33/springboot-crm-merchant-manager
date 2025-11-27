package merchant_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContactMerchantWithDetailsDTO {
    private Long id;
    private String field;
}
