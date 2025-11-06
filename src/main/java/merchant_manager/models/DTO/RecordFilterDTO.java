package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a filter condition for a specific field
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecordFilterDTO {
    private String fieldKey;
    private String operator; // EQUALS, CONTAINS, STARTS_WITH, ENDS_WITH, GREATER_THAN, LESS_THAN
    private String value;
}
