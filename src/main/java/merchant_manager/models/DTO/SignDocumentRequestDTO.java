package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.enums.RecipientStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignDocumentRequestDTO {
    private String token;
    private RecipientStatus status;
}
