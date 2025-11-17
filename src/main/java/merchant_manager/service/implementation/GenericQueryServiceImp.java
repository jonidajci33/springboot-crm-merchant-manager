package merchant_manager.service.implementation;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import merchant_manager.customExceptions.CustomExceptions;
import merchant_manager.dto.GenericFilterDTO;
import merchant_manager.dto.GenericQueryRequestDTO;
import merchant_manager.dto.GenericQueryResponseDTO;
import merchant_manager.models.User;
import merchant_manager.service.GenericQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GenericQueryServiceImp implements GenericQueryService {

    @PersistenceContext
    private EntityManager entityManager;

    private final UserServiceImp userServiceImp;

    @Override
    public GenericQueryResponseDTO<Object> executeQuery(GenericQueryRequestDTO request) {
        try {
            // Get the entity class
            Class<?> entityClass = getEntityClass(request.getEntityName());

            // Get logged user
            User loggedUser = userServiceImp.getLoggedUser();

            // Build the criteria query
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<?> query = cb.createQuery(entityClass);
            Root<?> root = query.from(entityClass);

            // Build predicates (filters)
            List<Predicate> predicates = new ArrayList<>();

            // ALWAYS add user filter (createdBy = logged user's username)
            predicates.add(cb.equal(root.get("createdBy"), loggedUser.getUsername()));

            // Add custom filters
            if (request.getFilters() != null) {
                for (GenericFilterDTO filter : request.getFilters()) {
                    Predicate predicate = buildPredicate(cb, root, filter);
                    if (predicate != null) {
                        predicates.add(predicate);
                    }
                }
            }

            // Apply all predicates
            query.where(cb.and(predicates.toArray(new Predicate[0])));

            // Apply sorting
            if (request.getSortBy() != null && !request.getSortBy().isEmpty()) {
                if ("DESC".equalsIgnoreCase(request.getSortDirection())) {
                    query.orderBy(cb.desc(root.get(request.getSortBy())));
                } else {
                    query.orderBy(cb.asc(root.get(request.getSortBy())));
                }
            }

            // Execute query for data
            TypedQuery<?> typedQuery = entityManager.createQuery(query);
            typedQuery.setFirstResult(request.getPage() * request.getSize());
            typedQuery.setMaxResults(request.getSize());
            List<?> data = typedQuery.getResultList();

            // Count total records
            CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
            Root<?> countRoot = countQuery.from(entityClass);
            countQuery.select(cb.count(countRoot));

            // Build predicates for count query (same as main query)
            List<Predicate> countPredicates = new ArrayList<>();
            countPredicates.add(cb.equal(countRoot.get("createdBy"), loggedUser.getUsername()));

            if (request.getFilters() != null) {
                for (GenericFilterDTO filter : request.getFilters()) {
                    Predicate predicate = buildPredicate(cb, countRoot, filter);
                    if (predicate != null) {
                        countPredicates.add(predicate);
                    }
                }
            }

            countQuery.where(cb.and(countPredicates.toArray(new Predicate[0])));
            long totalRecords = entityManager.createQuery(countQuery).getSingleResult();

            // Calculate pagination metadata
            int totalPages = (int) Math.ceil((double) totalRecords / request.getSize());
            boolean isFirst = request.getPage() == 0;
            boolean isLast = request.getPage() >= totalPages - 1;
            boolean hasNext = !isLast;
            boolean hasPrevious = !isFirst;

            // Build response
            @SuppressWarnings("unchecked")
            GenericQueryResponseDTO<Object> response = new GenericQueryResponseDTO<>();
            response.setData((List<Object>) (List<?>) data);
            response.setTotalRecords(totalRecords);
            response.setCurrentPage(request.getPage());
            response.setPageSize(request.getSize());
            response.setTotalPages(totalPages);
            response.setFirst(isFirst);
            response.setLast(isLast);
            response.setHasNext(hasNext);
            response.setHasPrevious(hasPrevious);

            return response;

        } catch (ClassNotFoundException e) {
            throw new CustomExceptions.ResourceNotFoundException("Entity class not found: " + request.getEntityName());
        } catch (Exception e) {
            throw new CustomExceptions.CustomValidationException("Error executing query: " + e.getMessage());
        }
    }

    /**
     * Build a predicate based on the filter operator
     */
    private Predicate buildPredicate(CriteriaBuilder cb, Root<?> root, GenericFilterDTO filter) {
        try {
            Path<Object> field = root.get(filter.getField());

            switch (filter.getOperator().toUpperCase()) {
                case "EQUALS":
                    return cb.equal(field, parseValue(field.getJavaType(), filter.getValue()));

                case "NOT_EQUALS":
                    return cb.notEqual(field, parseValue(field.getJavaType(), filter.getValue()));

                case "CONTAINS":
                    return cb.like(cb.lower(field.as(String.class)), "%" + filter.getValue().toLowerCase() + "%");

                case "STARTS_WITH":
                    return cb.like(cb.lower(field.as(String.class)), filter.getValue().toLowerCase() + "%");

                case "ENDS_WITH":
                    return cb.like(cb.lower(field.as(String.class)), "%" + filter.getValue().toLowerCase());

                case "GREATER_THAN":
                    return cb.greaterThan(field.as(Comparable.class), (Comparable) parseValue(field.getJavaType(), filter.getValue()));

                case "GREATER_THAN_OR_EQUAL":
                    return cb.greaterThanOrEqualTo(field.as(Comparable.class), (Comparable) parseValue(field.getJavaType(), filter.getValue()));

                case "LESS_THAN":
                    return cb.lessThan(field.as(Comparable.class), (Comparable) parseValue(field.getJavaType(), filter.getValue()));

                case "LESS_THAN_OR_EQUAL":
                    return cb.lessThanOrEqualTo(field.as(Comparable.class), (Comparable) parseValue(field.getJavaType(), filter.getValue()));

                case "IS_NULL":
                    return cb.isNull(field);

                case "IS_NOT_NULL":
                    return cb.isNotNull(field);

                case "IN":
                    String[] values = filter.getValue().split(",");
                    CriteriaBuilder.In<Object> inClause = cb.in(field);
                    for (String value : values) {
                        inClause.value(parseValue(field.getJavaType(), value.trim()));
                    }
                    return inClause;

                default:
                    return null;
            }
        } catch (Exception e) {
            // If field doesn't exist or error, return null predicate
            return null;
        }
    }

    /**
     * Parse string value to the appropriate type
     */
    private Object parseValue(Class<?> type, String value) {
        if (value == null) {
            return null;
        }

        if (type == String.class) {
            return value;
        } else if (type == Long.class || type == long.class) {
            return Long.parseLong(value);
        } else if (type == Integer.class || type == int.class) {
            return Integer.parseInt(value);
        } else if (type == Double.class || type == double.class) {
            return Double.parseDouble(value);
        } else if (type == Boolean.class || type == boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == LocalDateTime.class) {
            return LocalDateTime.parse(value);
        } else {
            return value;
        }
    }

    /**
     * Get entity class by name
     */
    private Class<?> getEntityClass(String entityName) throws ClassNotFoundException {
        return Class.forName("merchant_manager.models." + entityName);
    }
}
