package merchant_manager.service;

import merchant_manager.dto.GenericQueryRequestDTO;
import merchant_manager.dto.GenericQueryResponseDTO;

/**
 * Generic service for dynamic querying of any entity with filtering and pagination
 * Automatically includes user filter based on logged-in user
 */
public interface GenericQueryService {

    /**
     * Execute a dynamic query with filters and pagination
     * Automatically filters by createdBy = logged user
     *
     * @param request The query request with entity name, filters, and pagination
     * @return Paginated response with data and metadata
     */
    GenericQueryResponseDTO<Object> executeQuery(GenericQueryRequestDTO request);
}
