package merchant_manager.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DejavooTransactionResponse {
    private List<DejavooTransaction> transactions;
    private String status;
    private String message;
}
