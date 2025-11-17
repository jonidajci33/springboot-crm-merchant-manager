package merchant_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a filter condition for dynamic queries
 * Supports multiple operators for flexible filtering
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericFilterDTO {

    /**
     * The field name to filter on (e.g., "merchantName", "email", "status")
     */
    private String field;

    /**
     * The operator to use for comparison
     * Supported operators:
     * - EQUALS: Exact match (=)
     * - NOT_EQUALS: Not equal to (!=)
     * - CONTAINS: Contains substring (LIKE %value%)
     * - STARTS_WITH: Starts with (LIKE value%)
     * - ENDS_WITH: Ends with (LIKE %value)
     * - GREATER_THAN: Greater than (>)
     * - GREATER_THAN_OR_EQUAL: Greater than or equal (>=)
     * - LESS_THAN: Less than (<)
     * - LESS_THAN_OR_EQUAL: Less than or equal (<=)
     * - IN: Value in list (IN)
     * - NOT_IN: Value not in list (NOT IN)
     * - IS_NULL: Field is null
     * - IS_NOT_NULL: Field is not null
     */
    private String operator;

    /**
     * The value to filter by (can be null for IS_NULL/IS_NOT_NULL operators)
     */
    private String value;
}
