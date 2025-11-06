# Dynamic Records API Architecture

## Overview

The Dynamic Records API provides a flexible way to retrieve, filter, sort, and paginate records stored in an **Entity-Attribute-Value (EAV)** pattern. This architecture allows for dynamic form fields without requiring database schema changes.

## Table of Contents

1. [Data Model](#data-model)
2. [Architecture Flow](#architecture-flow)
3. [Components](#components)
4. [API Endpoints](#api-endpoints)
5. [Request/Response Examples](#requestresponse-examples)
6. [Performance Considerations](#performance-considerations)
7. [Future Enhancements](#future-enhancements)

---

## Data Model

### Entity Relationship Diagram

```
┌─────────────┐         ┌──────────────────────┐
│   Template  │────────>│ TemplateFormDefault  │
│             │         │  (Column Definitions)│
│ - id        │         │ - id                 │
│ - user_id   │         │ - template_id        │
│ - menu_id   │         │ - key                │
└─────────────┘         │ - label              │
                        │ - type               │
                        │ - priority           │
                        │ - options            │
                        └──────────────────────┘
                                  │
                                  │
                                  ▼
                        ┌───────────────────────────┐
                        │ TemplateFormValueDefault  │
                        │     (Field Values)        │
                        │ - id                      │
                        │ - template_form_default_id│
                        │ - record_id               │
                        │ - value                   │
                        └───────────────────────────┘
                                  │
                                  │
                                  ▼
                        ┌───────────────────┐
                        │ Actual Entities   │
                        │ (Lead, Contact,   │
                        │  Merchant, etc.)  │
                        │ - id (record_id)  │
                        └───────────────────┘
```

### Key Concepts

- **Template**: Associates a user with a menu and defines the structure
- **TemplateFormDefault**: Defines column/field metadata (what fields exist)
- **TemplateFormValueDefault**: Stores actual field values in EAV format
- **recordId**: Links values to actual entity records (Lead, Contact, Merchant)

### Example Data Structure

**TemplateFormDefault (Columns)**
```
| id | template_id | key          | label    | type  | priority |
|----|-------------|--------------|----------|-------|----------|
| 1  | 100         | name_abc123  | Name     | TEXT  | 1        |
| 2  | 100         | email_def456 | Email    | EMAIL | 2        |
| 3  | 100         | phone_ghi789 | Phone    | TEXT  | 3        |
```

**TemplateFormValueDefault (Values - EAV Format)**
```
| id | template_form_default_id | record_id | value              |
|----|--------------------------|-----------|-------------------|
| 1  | 1                        | 501       | John Doe          |
| 2  | 2                        | 501       | john@example.com  |
| 3  | 3                        | 501       | 555-1234          |
| 4  | 1                        | 502       | Jane Smith        |
| 5  | 2                        | 502       | jane@example.com  |
| 6  | 3                        | 502       | 555-5678          |
```

**Transformed Grid Format (Output)**
```json
[
  {
    "recordId": 501,
    "fields": {
      "name_abc123": "John Doe",
      "email_def456": "john@example.com",
      "phone_ghi789": "555-1234"
    }
  },
  {
    "recordId": 502,
    "fields": {
      "name_abc123": "Jane Smith",
      "email_def456": "jane@example.com",
      "phone_ghi789": "555-5678"
    }
  }
]
```

---

## Architecture Flow

### Complete Request Processing Flow

```
┌──────────────────────────────────────────────────────────────────┐
│                     1. CLIENT REQUEST                            │
│  POST /api/dynamic-records/query                                 │
│  {                                                                │
│    "templateId": 100,                                             │
│    "page": 0,                                                     │
│    "size": 10,                                                    │
│    "sortBy": "name_abc123",                                       │
│    "sortDirection": "ASC",                                        │
│    "filters": [...]                                               │
│  }                                                                │
└───────────────────────────┬──────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                  2. CONTROLLER LAYER                             │
│  DynamicRecordController.getDynamicRecords()                     │
│  - Validates request                                              │
│  - Passes to service layer                                        │
└───────────────────────────┬──────────────────────────────────────┘
                            │
                            ▼
┌──────────────────────────────────────────────────────────────────┐
│                  3. SERVICE LAYER                                │
│  DynamicRecordServiceImp.getDynamicRecords()                     │
└───────────────────────────┬──────────────────────────────────────┘
                            │
                ┌───────────┴───────────┐
                ▼                       ▼
    ┌─────────────────────┐   ┌─────────────────────┐
    │ 3a. FETCH COLUMNS   │   │ 3b. FETCH RECORD IDs│
    │ - Get all           │   │ - Get distinct      │
    │   TemplateForm      │   │   record IDs for    │
    │   Default for       │   │   template          │
    │   template          │   │ - Ordered by ID     │
    │ - Ordered by        │   │                     │
    │   priority          │   │ Query:              │
    │                     │   │ SELECT DISTINCT     │
    │ Result: List of     │   │   record_id         │
    │ column definitions  │   │ FROM tfvd           │
    └─────────────────────┘   │ WHERE template_id   │
                              └──────────┬──────────┘
                                         │
                                         ▼
                            ┌─────────────────────────┐
                            │ 4. APPLY FILTERS        │
                            │ For each filter:        │
                            │ - Get values for field  │
                            │ - Apply operator:       │
                            │   * EQUALS              │
                            │   * CONTAINS            │
                            │   * STARTS_WITH         │
                            │   * ENDS_WITH           │
                            │   * GREATER_THAN        │
                            │   * LESS_THAN           │
                            │ - Keep matching IDs     │
                            │                         │
                            │ Result: Filtered IDs    │
                            │ [501, 503, 507, ...]    │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 5. APPLY SORTING        │
                            │ If sortBy specified:    │
                            │ - Get values for sort   │
                            │   field                 │
                            │ - Create Map<ID,Value>  │
                            │ - Sort IDs by values    │
                            │ - Apply direction       │
                            │   (ASC/DESC)            │
                            │                         │
                            │ Result: Sorted IDs      │
                            │ [501, 503, 507, ...]    │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 6. APPLY PAGINATION     │
                            │ - Calculate:            │
                            │   * totalRecords        │
                            │   * totalPages          │
                            │   * fromIndex           │
                            │   * toIndex             │
                            │ - Slice ID list         │
                            │                         │
                            │ Result: Paginated IDs   │
                            │ [501, 503] (page 0)     │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 7. FETCH VALUES         │
                            │ Query:                  │
                            │ SELECT * FROM tfvd      │
                            │ WHERE template_id = ?   │
                            │   AND record_id IN (?)  │
                            │                         │
                            │ Result: All values for  │
                            │ paginated records       │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 8. TRANSFORM TO GRID    │
                            │ - Group values by       │
                            │   record_id             │
                            │ - For each record:      │
                            │   * Create field map    │
                            │   * key -> value        │
                            │ - Convert vertical      │
                            │   (EAV) to horizontal   │
                            │   (Grid)                │
                            │                         │
                            │ Result: Grid-ready data │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 9. BUILD RESPONSE       │
                            │ {                       │
                            │   records: [...],       │
                            │   columns: [...],       │
                            │   totalRecords: 100,    │
                            │   currentPage: 0,       │
                            │   pageSize: 10,         │
                            │   totalPages: 10        │
                            │ }                       │
                            └──────────┬──────────────┘
                                       │
                                       ▼
                            ┌─────────────────────────┐
                            │ 10. RETURN TO CLIENT    │
                            │ HTTP 200 OK             │
                            │ JSON Response           │
                            └─────────────────────────┘
```

### Step-by-Step Explanation

#### Step 1: Client Request
The client sends a POST request with:
- `templateId`: Which template/form to query
- `page` & `size`: Pagination parameters
- `sortBy` & `sortDirection`: Optional sorting
- `filters`: Array of filter conditions

#### Step 2: Controller Layer
- **DynamicRecordController** receives the request
- Validates the `@Valid` DTO
- Delegates to service layer

#### Step 3a: Fetch Column Definitions
```sql
SELECT * FROM template_form_default
WHERE template_id = ?
ORDER BY priority ASC
```
This gives us the schema/structure of the grid.

#### Step 3b: Fetch All Record IDs
```sql
SELECT DISTINCT record_id
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfd.template_id = ?
ORDER BY record_id DESC
```
Get all unique records for this template.

#### Step 4: Apply Filters
For each filter, we:
1. Fetch values for the specific field
2. Apply the operator (EQUALS, CONTAINS, etc.)
3. Keep only matching record IDs

**Example:**
```
Filter: { fieldKey: "name_abc123", operator: "CONTAINS", value: "John" }

Query values → Filter → Keep IDs [501, 508, 512]
```

Multiple filters are applied sequentially (AND logic).

#### Step 5: Apply Sorting
If `sortBy` is specified:
1. Fetch values for the sort field
2. Create a Map<RecordId, Value>
3. Sort the record IDs based on values
4. Apply direction (ASC/DESC)

#### Step 6: Apply Pagination
```java
totalRecords = filteredIds.size()
totalPages = ceil(totalRecords / pageSize)
fromIndex = page * pageSize
toIndex = min(fromIndex + pageSize, totalRecords)
paginatedIds = filteredIds.subList(fromIndex, toIndex)
```

#### Step 7: Fetch Values for Paginated Records
```sql
SELECT tfvd.*
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfd.template_id = ?
  AND tfvd.record_id IN (?, ?, ?)
```
Only fetch data for the current page.

#### Step 8: Transform EAV to Grid
```java
// Group by record_id
Map<Long, List<Value>> valuesByRecord = ...

// Transform each record
for (recordId : paginatedIds) {
    Map<String, String> fields = new HashMap<>();
    for (value : valuesByRecord.get(recordId)) {
        fields.put(value.field.key, value.value);
    }
    records.add(new DynamicRecordDTO(recordId, fields));
}
```

#### Step 9: Build Response
Combine:
- Transformed records
- Column definitions
- Pagination metadata

#### Step 10: Return to Client
Send HTTP 200 with JSON response.

---

## Components

### 1. DTOs (Data Transfer Objects)

#### DynamicRecordsRequestDTO
```java
{
    Long templateId;           // Required
    int page = 0;              // Default: 0
    int size = 10;             // Default: 10
    List<RecordFilterDTO> filters;  // Optional
    String sortBy;             // Optional field key
    String sortDirection = "ASC";   // ASC or DESC
}
```

#### RecordFilterDTO
```java
{
    String fieldKey;    // Field to filter on
    String operator;    // EQUALS, CONTAINS, STARTS_WITH, ENDS_WITH, GREATER_THAN, LESS_THAN
    String value;       // Filter value
}
```

#### DynamicRecordDTO
```java
{
    Long recordId;              // Entity ID (Lead, Contact, etc.)
    Map<String, String> fields; // key -> value mapping
}
```

#### DynamicRecordsPageDTO
```java
{
    List<DynamicRecordDTO> records;
    List<TemplateFormDefault> columns;
    long totalRecords;
    int currentPage;
    int pageSize;
    int totalPages;
}
```

### 2. Repository Layer

#### TemplateFormDefaultRepository
```java
- findAllByTemplateId(templateId)      // Get column definitions
- findByKey(key)                        // Find column by key
- findByTemplateIdOrderByPriorityAsc() // Ordered columns
```

#### TemplateFormValueDefaultRepository
```java
- findDistinctRecordIdsByTemplateId()  // Get all record IDs
- findByTemplateIdAndRecordIds()       // Fetch values for records
- findByRecordId()                     // Get all values for one record
```

### 3. Service Layer

#### DynamicRecordServiceImp

**Main Method:**
```java
getDynamicRecords(request) -> DynamicRecordsPageDTO
```

**Helper Methods:**
- `applyFilters()` - Filter record IDs based on conditions
- `filterByField()` - Apply single filter
- `matchesFilter()` - Check if value matches filter
- `applySorting()` - Sort record IDs by field value
- `transformToGridFormat()` - Convert EAV to grid rows

### 4. Controller Layer

#### DynamicRecordController

**Endpoints:**
```java
POST /api/dynamic-records/query
  → Advanced query with filters

GET /api/dynamic-records/template/{templateId}
  → Simple query with pagination only
```

---

## API Endpoints

### 1. Advanced Query (POST)

**Endpoint:** `POST /api/dynamic-records/query`

**Request Body:**
```json
{
  "templateId": 100,
  "page": 0,
  "size": 20,
  "sortBy": "name_field_key",
  "sortDirection": "ASC",
  "filters": [
    {
      "fieldKey": "status_key",
      "operator": "EQUALS",
      "value": "Active"
    },
    {
      "fieldKey": "name_field_key",
      "operator": "CONTAINS",
      "value": "John"
    }
  ]
}
```

**Response:**
```json
{
  "records": [
    {
      "recordId": 501,
      "fields": {
        "name_field_key": "John Doe",
        "email_field_key": "john@example.com",
        "status_key": "Active"
      }
    }
  ],
  "columns": [
    {
      "id": 1,
      "key": "name_field_key",
      "label": "Name",
      "type": "TEXT",
      "priority": 1,
      "options": null,
      "style": null
    }
  ],
  "totalRecords": 15,
  "currentPage": 0,
  "pageSize": 20,
  "totalPages": 1
}
```

### 2. Simple Query (GET)

**Endpoint:** `GET /api/dynamic-records/template/{templateId}?page=0&size=10`

**Response:** Same as POST endpoint

---

## Request/Response Examples

### Example 1: Basic Pagination

**Request:**
```http
GET /api/dynamic-records/template/100?page=0&size=5
Authorization: Bearer {jwt_token}
```

**Response:**
```json
{
  "records": [
    {
      "recordId": 501,
      "fields": {
        "company_name": "Acme Corp",
        "email": "contact@acme.com",
        "phone": "555-0100"
      }
    },
    {
      "recordId": 502,
      "fields": {
        "company_name": "TechStart Inc",
        "email": "info@techstart.com",
        "phone": "555-0200"
      }
    }
  ],
  "columns": [...],
  "totalRecords": 50,
  "currentPage": 0,
  "pageSize": 5,
  "totalPages": 10
}
```

### Example 2: Filtering by Status

**Request:**
```http
POST /api/dynamic-records/query
Content-Type: application/json
Authorization: Bearer {jwt_token}

{
  "templateId": 100,
  "page": 0,
  "size": 10,
  "filters": [
    {
      "fieldKey": "status_field",
      "operator": "EQUALS",
      "value": "Active"
    }
  ]
}
```

### Example 3: Search and Sort

**Request:**
```http
POST /api/dynamic-records/query
Content-Type: application/json

{
  "templateId": 100,
  "page": 0,
  "size": 10,
  "sortBy": "company_name",
  "sortDirection": "ASC",
  "filters": [
    {
      "fieldKey": "company_name",
      "operator": "CONTAINS",
      "value": "Tech"
    }
  ]
}
```

### Example 4: Numeric Filtering

**Request:**
```http
POST /api/dynamic-records/query

{
  "templateId": 100,
  "filters": [
    {
      "fieldKey": "revenue_field",
      "operator": "GREATER_THAN",
      "value": "1000000"
    },
    {
      "fieldKey": "employees_field",
      "operator": "LESS_THAN",
      "value": "500"
    }
  ]
}
```

---

## Performance Considerations

### 1. Query Optimization

**Current Approach:**
- Fetches all record IDs first
- Applies filtering in-memory
- Fetches values only for paginated records

**Pros:**
- Flexible filtering on any field
- Works with complex filter combinations
- Only fetches needed data for current page

**Cons:**
- In-memory filtering for large datasets
- Multiple database queries

### 2. Indexing Recommendations

```sql
-- Index on template_form_default
CREATE INDEX idx_tfd_template_id ON template_form_default(template_id);
CREATE INDEX idx_tfd_key ON template_form_default(key);

-- Index on template_form_value_default
CREATE INDEX idx_tfvd_record_id ON template_form_value_default(record_id);
CREATE INDEX idx_tfvd_tfd_id ON template_form_value_default(template_form_default_id);
CREATE INDEX idx_tfvd_composite ON template_form_value_default(template_form_default_id, record_id);

-- Composite index for filtering
CREATE INDEX idx_tfvd_value_search ON template_form_value_default(template_form_default_id, value);
```

### 3. Caching Strategies

**Column Definitions:**
```java
@Cacheable("template-columns")
public List<TemplateFormDefault> getColumns(Long templateId) {
    // Column definitions rarely change
}
```

**Pagination Results:**
```java
@Cacheable(value = "dynamic-records",
           key = "#request.templateId + '-' + #request.page + '-' + #request.size")
```

### 4. Performance Tips

1. **Limit Page Size**: Enforce maximum page size (e.g., 100 records)
2. **Use Indexes**: Ensure proper database indexing
3. **Cache Column Definitions**: They rarely change
4. **Batch Fetch Values**: Fetch all values for a page in one query
5. **Optimize Filters**: Consider database-level filtering for common cases

### 5. Scalability Considerations

**For Large Datasets (>100,000 records):**

Option 1: **Database-Level Filtering**
```sql
-- Use native SQL with dynamic WHERE clauses
SELECT DISTINCT tfvd.record_id
FROM template_form_value_default tfvd
WHERE tfvd.template_form_default_id = :fieldId
  AND tfvd.value LIKE :filterValue
```

Option 2: **Elasticsearch/Search Engine**
- Index records for full-text search
- Better performance for complex queries
- Near real-time updates

Option 3: **Materialized Views**
- Pre-compute common queries
- Refresh periodically
- Trade consistency for performance

---

## Future Enhancements

### 1. Advanced Filtering

- **OR Logic**: Support filter groups with OR conditions
- **Date Ranges**: Special handling for date fields
- **IN Operator**: Filter by multiple values
- **NOT Operator**: Exclusion filters
- **NULL/Empty**: Filter records with empty values

```json
{
  "filters": {
    "and": [
      {
        "or": [
          {"fieldKey": "status", "operator": "EQUALS", "value": "Active"},
          {"fieldKey": "status", "operator": "EQUALS", "value": "Pending"}
        ]
      },
      {"fieldKey": "created_date", "operator": "BETWEEN", "value": ["2024-01-01", "2024-12-31"]}
    ]
  }
}
```

### 2. Aggregations

```json
{
  "aggregations": {
    "totalRevenue": {"field": "revenue", "operation": "SUM"},
    "avgEmployees": {"field": "employees", "operation": "AVG"},
    "statusCount": {"field": "status", "operation": "COUNT"}
  }
}
```

### 3. Export Functionality

- Export to CSV
- Export to Excel
- Export to PDF
- Async export for large datasets

### 4. Saved Filters

- Save commonly used filter combinations
- Share filters between users
- Quick filter presets

### 5. Real-Time Updates

- WebSocket notifications
- Live grid updates
- Optimistic UI updates

### 6. Audit Trail

- Track who viewed records
- Log filter queries
- Performance monitoring

### 7. Multi-Template Queries

- Query across multiple templates
- Join data from different sources
- Unified view

---

## Troubleshooting

### Common Issues

**1. Empty Results**
- Check if templateId exists
- Verify records exist for template
- Check filter conditions

**2. Slow Performance**
- Add database indexes
- Reduce page size
- Optimize filters
- Check query execution plans

**3. Missing Fields**
- Verify column definitions exist
- Check template_form_default_id references
- Validate data consistency

**4. Incorrect Pagination**
- Verify page and size parameters
- Check totalRecords calculation
- Validate sorting logic

---

## Conclusion

The Dynamic Records API provides a flexible, scalable solution for managing dynamic form data. By using the EAV pattern with efficient querying and transformation, it supports:

- ✅ Dynamic schema (no database changes needed)
- ✅ Flexible filtering on any field
- ✅ Sorting and pagination
- ✅ Grid-ready data format
- ✅ Extensible architecture

This architecture can handle various use cases including CRM systems, form builders, and dynamic data grids.

---

**Document Version:** 1.0
**Last Updated:** 2025-11-06
**Author:** Merchant Manager Team
