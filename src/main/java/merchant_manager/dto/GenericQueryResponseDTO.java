package merchant_manager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generic paginated response for dynamic queries
 * Contains the data and pagination metadata
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GenericQueryResponseDTO<T> {

    /**
     * The list of records returned by the query
     */
    private List<T> data;

    /**
     * Total number of records matching the filters
     */
    private long totalRecords;

    /**
     * Current page number (0-based)
     */
    private int currentPage;

    /**
     * Page size
     */
    private int pageSize;

    /**
     * Total number of pages
     */
    private int totalPages;

    /**
     * Whether this is the first page
     */
    private boolean first;

    /**
     * Whether this is the last page
     */
    private boolean last;

    /**
     * Whether there is a next page
     */
    private boolean hasNext;

    /**
     * Whether there is a previous page
     */
    private boolean hasPrevious;

    public void setData(List<T> data) {
        this.data = data;
    }
}
