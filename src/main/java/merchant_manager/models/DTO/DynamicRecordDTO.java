package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * Represents a single record with its dynamic field values
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRecordDTO {
    private Long recordId;
    private Map<String, String> fields; // key -> value mapping
}
