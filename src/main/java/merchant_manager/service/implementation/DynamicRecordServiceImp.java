package merchant_manager.service.implementation;

import lombok.RequiredArgsConstructor;
import merchant_manager.models.DTO.*;
import merchant_manager.models.Template;
import merchant_manager.models.TemplateFormDefault;
import merchant_manager.models.TemplateFormValueDefault;
import merchant_manager.repository.TemplateFormDefaultRepository;
import merchant_manager.repository.TemplateFormValueDefaultRepository;
import merchant_manager.service.DynamicRecordService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DynamicRecordServiceImp implements DynamicRecordService {

    private final TemplateFormDefaultRepository templateFormDefaultRepository;
    private final TemplateFormValueDefaultRepository templateFormValueDefaultRepository;
    private final TemplateServiceImp templateServiceImp;
    private final UserServiceImp userServiceImp;

    @Override
    public DynamicRecordsSimplePageDTO getDynamicRecordsSimple(DynamicRecordsRequestDTO request) {
        Long userId = userServiceImp.getLoggedUser().getId();
        Template template = templateServiceImp.findByUserIdAndMenuId(userId, request.getMenuId());
        // 1. Get all column definitions for the template (for transformation only)
        List<TemplateFormDefault> columns = templateFormDefaultRepository
                .findAllByTemplateId(template.getId());

        // 2. Get all distinct record IDs for this template
        List<Long> allRecordIds = templateFormValueDefaultRepository
                .findDistinctRecordIdsByTemplateId(template.getId());

        // 3. Apply filtering
        List<Long> filteredRecordIds = applyFilters(allRecordIds, request.getFilters(), template.getId());

        // 4. Apply sorting (if specified)
        filteredRecordIds = applySorting(filteredRecordIds, request.getSortBy(),
                                        request.getSortDirection(), template.getId());

        // 5. Calculate pagination
        int totalRecords = filteredRecordIds.size();
        int totalPages = (int) Math.ceil((double) totalRecords / request.getSize());
        int fromIndex = request.getPage() * request.getSize();
        int toIndex = Math.min(fromIndex + request.getSize(), totalRecords);

        // 6. Get paginated record IDs
        List<Long> paginatedRecordIds = filteredRecordIds.subList(fromIndex, toIndex);

        // 7. Fetch all values for the paginated records
        List<TemplateFormValueDefault> values = templateFormValueDefaultRepository
                .findByTemplateIdAndRecordIds(template.getId(), paginatedRecordIds);

        // 8. Transform EAV data to grid format
        List<DynamicRecordDTO> records = transformToGridFormat(paginatedRecordIds, values, columns);

        // 9. Build and return response (without columns)
        return new DynamicRecordsSimplePageDTO(
                records,
                totalRecords,
                request.getPage(),
                request.getSize(),
                totalPages
        );
    }

    /**
     * Apply filters to record IDs
     */
    private List<Long> applyFilters(List<Long> recordIds, List<RecordFilterDTO> filters, Long templateId) {
        if (filters == null || filters.isEmpty()) {
            return recordIds;
        }

        // For each filter, narrow down the record IDs
        for (RecordFilterDTO filter : filters) {
            recordIds = filterByField(recordIds, filter, templateId);
        }

        return recordIds;
    }

    /**
     * Filter record IDs by a specific field condition
     */
    private List<Long> filterByField(List<Long> recordIds, RecordFilterDTO filter, Long templateId) {
        if (recordIds.isEmpty()) {
            return recordIds;
        }

        // Get all values for this field and the given record IDs
        List<TemplateFormValueDefault> values = templateFormValueDefaultRepository
                .findByTemplateIdAndRecordIds(templateId, recordIds);

        // Filter by field key
        return values.stream()
                .filter(v -> v.getTemplateFormDefault().getKey().equals(filter.getFieldKey()))
                .filter(v -> matchesFilter(v.getValue(), filter))
                .map(TemplateFormValueDefault::getRecordId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Check if a value matches the filter condition
     */
    private boolean matchesFilter(String value, RecordFilterDTO filter) {
        if (value == null || filter.getValue() == null) {
            return false;
        }

        String lowerValue = value.toLowerCase();
        String lowerFilterValue = filter.getValue().toLowerCase();

        return switch (filter.getOperator().toUpperCase()) {
            case "EQUALS" -> lowerValue.equals(lowerFilterValue);
            case "CONTAINS" -> lowerValue.contains(lowerFilterValue);
            case "STARTS_WITH" -> lowerValue.startsWith(lowerFilterValue);
            case "ENDS_WITH" -> lowerValue.endsWith(lowerFilterValue);
            case "GREATER_THAN" -> {
                try {
                    yield Double.parseDouble(value) > Double.parseDouble(filter.getValue());
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            case "LESS_THAN" -> {
                try {
                    yield Double.parseDouble(value) < Double.parseDouble(filter.getValue());
                } catch (NumberFormatException e) {
                    yield false;
                }
            }
            default -> true;
        };
    }

    /**
     * Apply sorting to record IDs
     */
    private List<Long> applySorting(List<Long> recordIds, String sortBy, String sortDirection, Long templateId) {
        if (sortBy == null || sortBy.isEmpty() || recordIds.isEmpty()) {
            return recordIds;
        }

        // Get all values for the sort field
        List<TemplateFormValueDefault> values = templateFormValueDefaultRepository
                .findByTemplateIdAndRecordIds(templateId, recordIds);

        // Create a map of recordId -> value for the sort field
        Map<Long, String> sortValues = values.stream()
                .filter(v -> v.getTemplateFormDefault().getKey().equals(sortBy))
                .collect(Collectors.toMap(
                        TemplateFormValueDefault::getRecordId,
                        TemplateFormValueDefault::getValue,
                        (v1, v2) -> v1
                ));

        // Sort record IDs based on the values
        Comparator<Long> comparator = Comparator.comparing(
                id -> sortValues.getOrDefault(id, ""),
                String.CASE_INSENSITIVE_ORDER
        );

        if ("DESC".equalsIgnoreCase(sortDirection)) {
            comparator = comparator.reversed();
        }

        return recordIds.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    /**
     * Transform EAV data to grid format (horizontal rows)
     */
    private List<DynamicRecordDTO> transformToGridFormat(
            List<Long> recordIds,
            List<TemplateFormValueDefault> values,
            List<TemplateFormDefault> columns
    ) {
        // Group values by record ID
        Map<Long, List<TemplateFormValueDefault>> valuesByRecord = values.stream()
                .collect(Collectors.groupingBy(TemplateFormValueDefault::getRecordId));

        // Transform each record
        return recordIds.stream()
                .map(recordId -> {
                    Map<String, String> fieldValues = new HashMap<>();

                    // Get values for this record
                    List<TemplateFormValueDefault> recordValues = valuesByRecord.getOrDefault(recordId, Collections.emptyList());

                    // Map each value to its field key
                    for (TemplateFormValueDefault value : recordValues) {
                        fieldValues.put(
                                value.getTemplateFormDefault().getKey(),
                                value.getValue()
                        );
                    }

                    return new DynamicRecordDTO(recordId, fieldValues);
                })
                .collect(Collectors.toList());
    }
}
