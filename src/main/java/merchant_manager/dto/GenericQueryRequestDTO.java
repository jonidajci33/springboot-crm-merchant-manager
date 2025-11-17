package merchant_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for generic dynamic queries with filtering and pagination
 * Automatically filters by logged-in user's ID
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericQueryRequestDTO {

    /**
     * The entity name to query (e.g., "Merchant", "Contact", "Lead")
     */
    private String entityName;

    /**
     * List of filters to apply (user filter is automatically added)
     */
    private List<GenericFilterDTO> filters;

    /**
     * Page number (0-based)
     */
    private int page = 0;

    /**
     * Page size
     */
    private int size = 10;

    /**
     * Field to sort by (e.g., "createdAt", "merchantName")
     */
    private String sortBy;

    /**
     * Sort direction: ASC or DESC
     */
    private String sortDirection = "ASC";
}
