# Dynamic Records API - Quick Start Guide

## 🚀 Getting Started in 5 Minutes

### 1. Understanding the Basics

Your system uses **dynamic forms** where:
- **TemplateFormDefault** = Column definitions (like "Name", "Email", "Phone")
- **TemplateFormValueDefault** = Actual data values
- **recordId** = Links to your entities (Lead, Contact, Merchant)

### 2. Basic API Call

**Get records for a template (simple):**

```bash
curl -X GET "http://localhost:8445/api/dynamic-records/template/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Response:**
```json
{
  "records": [
    {
      "recordId": 101,
      "fields": {
        "name_key": "John Doe",
        "email_key": "john@example.com"
      }
    }
  ],
  "columns": [...],
  "totalRecords": 25,
  "currentPage": 0,
  "pageSize": 10,
  "totalPages": 3
}
```

### 3. Advanced Query with Filters

**Search for specific records:**

```bash
curl -X POST "http://localhost:8445/api/dynamic-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "templateId": 1,
    "page": 0,
    "size": 10,
    "sortBy": "name_key",
    "sortDirection": "ASC",
    "filters": [
      {
        "fieldKey": "status_key",
        "operator": "EQUALS",
        "value": "Active"
      }
    ]
  }'
```

### 4. Available Filter Operators

| Operator | Description | Example |
|----------|-------------|---------|
| `EQUALS` | Exact match (case-insensitive) | `"operator": "EQUALS", "value": "Active"` |
| `CONTAINS` | Contains substring | `"operator": "CONTAINS", "value": "Tech"` |
| `STARTS_WITH` | Starts with string | `"operator": "STARTS_WITH", "value": "Mr"` |
| `ENDS_WITH` | Ends with string | `"operator": "ENDS_WITH", "value": ".com"` |
| `GREATER_THAN` | Numeric comparison | `"operator": "GREATER_THAN", "value": "1000"` |
| `LESS_THAN` | Numeric comparison | `"operator": "LESS_THAN", "value": "500"` |

### 5. Frontend Integration (React/TypeScript Example)

```typescript
interface DynamicRecordsRequest {
  templateId: number;
  page: number;
  size: number;
  sortBy?: string;
  sortDirection?: 'ASC' | 'DESC';
  filters?: Array<{
    fieldKey: string;
    operator: string;
    value: string;
  }>;
}

const fetchDynamicRecords = async (request: DynamicRecordsRequest) => {
  const response = await fetch('/api/dynamic-records/query', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      'Authorization': `Bearer ${token}`
    },
    body: JSON.stringify(request)
  });

  return await response.json();
};

// Usage
const data = await fetchDynamicRecords({
  templateId: 1,
  page: 0,
  size: 20,
  sortBy: 'name_field',
  sortDirection: 'ASC',
  filters: [
    { fieldKey: 'status', operator: 'EQUALS', value: 'Active' }
  ]
});

// Build data grid
const columns = data.columns.map(col => ({
  field: col.key,
  headerName: col.label,
  sortable: true
}));

const rows = data.records.map(record => ({
  id: record.recordId,
  ...record.fields
}));
```

### 6. Common Use Cases

#### Use Case 1: Search by Name
```json
{
  "templateId": 1,
  "page": 0,
  "size": 10,
  "filters": [
    {
      "fieldKey": "name_field_key",
      "operator": "CONTAINS",
      "value": "John"
    }
  ]
}
```

#### Use Case 2: Filter Active Records, Sorted by Date
```json
{
  "templateId": 1,
  "page": 0,
  "size": 10,
  "sortBy": "created_date_key",
  "sortDirection": "DESC",
  "filters": [
    {
      "fieldKey": "is_active_key",
      "operator": "EQUALS",
      "value": "true"
    }
  ]
}
```

#### Use Case 3: Multiple Filters (AND logic)
```json
{
  "templateId": 1,
  "filters": [
    {
      "fieldKey": "status_key",
      "operator": "EQUALS",
      "value": "Active"
    },
    {
      "fieldKey": "revenue_key",
      "operator": "GREATER_THAN",
      "value": "100000"
    },
    {
      "fieldKey": "city_key",
      "operator": "EQUALS",
      "value": "New York"
    }
  ]
}
```

### 7. Testing with Swagger UI

1. Start your application
2. Navigate to: `http://localhost:8445/swagger-ui.html`
3. Find "Dynamic Records" section
4. Click "Try it out"
5. Enter your request parameters
6. Click "Execute"

### 8. Response Structure

```typescript
interface DynamicRecordsResponse {
  records: Array<{
    recordId: number;
    fields: Record<string, string>;  // key-value pairs
  }>;
  columns: Array<{
    id: number;
    key: string;
    label: string;
    type: string;
    priority: number;
    options?: Record<string, string>;
    style?: Record<string, string>;
  }>;
  totalRecords: number;
  currentPage: number;
  pageSize: number;
  totalPages: number;
}
```

### 9. Error Handling

```typescript
try {
  const data = await fetchDynamicRecords(request);
  // Handle success
} catch (error) {
  if (error.status === 401) {
    // Handle unauthorized - redirect to login
  } else if (error.status === 404) {
    // Template not found
  } else if (error.status === 400) {
    // Invalid request parameters
  } else {
    // General error
  }
}
```

### 10. Performance Tips

- **Limit Page Size**: Keep `size` <= 100 for best performance
- **Use Specific Filters**: More filters = fewer results = faster
- **Index Your Data**: Ensure database indexes are in place
- **Cache Column Definitions**: They rarely change
- **Avoid Deep Pagination**: Use cursor-based pagination for very large datasets

---

## 📚 Next Steps

- Read the full [Architecture Documentation](./DynamicRecordsArchitecture.md)
- Check [API Examples](./APIExamples.md) for more scenarios
- Review [Performance Optimization Guide](./PerformanceOptimization.md)

## 🆘 Need Help?

- Check Swagger UI for interactive documentation
- Review error messages in API responses
- Check application logs for detailed debugging

---

**Happy Coding! 🎉**
