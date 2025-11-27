package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.Document;
import merchant_manager.models.Recipient;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipientDocumentResponseDTO {
    private Recipient recipient;
    private Document document;
}
