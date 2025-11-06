package merchant_manager.models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import merchant_manager.models.TemplateFormDefault;

import java.util.List;

/**
 * Paginated response for dynamic records
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DynamicRecordsPageDTO {
    private List<DynamicRecordDTO> records;
    private List<TemplateFormDefault> columns; // Column definitions
    private long totalRecords;
    private int currentPage;
    private int pageSize;
    private int totalPages;
}
