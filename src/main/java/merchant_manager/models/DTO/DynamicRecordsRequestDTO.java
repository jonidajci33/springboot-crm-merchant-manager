package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request DTO for fetching dynamic records with filtering and pagination
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRecordsRequestDTO {
    private Long menuId;
    private int page = 0;
    private int size = 10;
    private List<RecordFilterDTO> filters;
    private String sortBy; // field key to sort by
    private String sortDirection = "ASC"; // ASC or DESC
}
