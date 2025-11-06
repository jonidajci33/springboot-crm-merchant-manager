package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Simplified paginated response for dynamic records (without column definitions)
 * Use GET /api/template-forms-default/template/{id} to fetch columns separately
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRecordsSimplePageDTO {
    private List<DynamicRecordDTO> records;
    private long totalRecords;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
