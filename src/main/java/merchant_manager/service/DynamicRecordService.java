package merchant_manager.service;

import merchant_manager.models.DTO.DynamicRecordsPageDTO;
import merchant_manager.models.DTO.DynamicRecordsRequestDTO;
import merchant_manager.models.DTO.DynamicRecordsSimplePageDTO;

public interface DynamicRecordService {

    /**
     * Retrieves dynamic records with filtering and pagination (without columns)
     * Fetch columns separately using GET /api/template-forms-default/template/{id}
     *
     * @param request The request containing template ID, filters, and pagination info
     * @return Paginated dynamic records without column definitions
     */
    DynamicRecordsSimplePageDTO getDynamicRecordsSimple(DynamicRecordsRequestDTO request);
}
