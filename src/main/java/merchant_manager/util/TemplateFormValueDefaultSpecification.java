package merchant_manager.util;

import jakarta.persistence.criteria.Predicate;
import merchant_manager.models.DTO.RecordFilterDTO;
import merchant_manager.models.TemplateFormValueDefault;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class TemplateFormValueDefaultSpecification {

    /**
     * Creates a specification for filtering TemplateFormValueDefault records
     * based on field key and filter criteria
     */
    public static Specification<TemplateFormValueDefault> filterByFieldAndValue(
            String fieldKey,
            RecordFilterDTO filter,
            List<Long> recordIds) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by field key
            predicates.add(criteriaBuilder.equal(
                    root.get("templateFormDefault").get("key"),
                    fieldKey
            ));

            // Filter by record IDs if provided
            if (recordIds != null && !recordIds.isEmpty()) {
                predicates.add(root.get("recordId").in(recordIds));
            }

            // Apply filter based on operator
            if (filter != null && filter.getValue() != null) {
                String value = filter.getValue();
                String operator = filter.getOperator() != null ? filter.getOperator().toUpperCase() : "EQUALS";

                switch (operator) {
                    case "EQUALS":
                        predicates.add(criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("value")),
                                value.toLowerCase()
                        ));
                        break;

                    case "CONTAINS":
                        predicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("value")),
                                "%" + value.toLowerCase() + "%"
                        ));
                        break;

                    case "STARTS_WITH":
                        predicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("value")),
                                value.toLowerCase() + "%"
                        ));
                        break;

                    case "ENDS_WITH":
                        predicates.add(criteriaBuilder.like(
                                criteriaBuilder.lower(root.get("value")),
                                "%" + value.toLowerCase()
                        ));
                        break;

                    case "GREATER_THAN":
                    case "LESS_THAN":
                    case "GREATER_THAN_OR_EQUAL":
                    case "LESS_THAN_OR_EQUAL":
                        // Numeric operators are handled in-service to avoid DB cast errors on non-numeric values.
                        predicates.add(criteriaBuilder.disjunction());
                        break;

                    default:
                        // Default to EQUALS
                        predicates.add(criteriaBuilder.equal(
                                criteriaBuilder.lower(root.get("value")),
                                value.toLowerCase()
                        ));
                        break;
                }
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    /**
     * Creates a specification for fetching values by template ID and record IDs
     * for sorting purposes
     */
    public static Specification<TemplateFormValueDefault> filterForSorting(
            String sortByKey,
            List<Long> recordIds) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Filter by field key
            predicates.add(criteriaBuilder.equal(
                    root.get("templateFormDefault").get("key"),
                    sortByKey
            ));

            // Filter by record IDs
            if (recordIds != null && !recordIds.isEmpty()) {
                predicates.add(root.get("recordId").in(recordIds));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
