# Merchant Manager Documentation

Welcome to the Merchant Manager API documentation.

## 📖 Documentation Index

### Core Documentation

1. **[Quick Start Guide](./QuickStartGuide.md)** - Get started in 5 minutes
   - Basic API calls
   - Common use cases
   - Frontend integration examples

2. **[Separated APIs Guide](./SeparatedAPIsGuide.md)** - **⭐ RECOMMENDED** Separate columns and records
   - Why separate APIs?
   - Performance benefits
   - Complete frontend examples with caching
   - Migration guide from old API

3. **[Dynamic Records Architecture](./DynamicRecordsArchitecture.md)** - Complete architecture guide
   - Data model explanation
   - Detailed architecture flow
   - Component descriptions
   - Performance considerations
   - Future enhancements

## 🎯 What is the Dynamic Records API?

The Dynamic Records API allows you to:

- ✅ Retrieve records with **dynamic fields** (EAV pattern)
- ✅ Apply **flexible filters** on any field
- ✅ **Sort** by any column
- ✅ **Paginate** large datasets efficiently
- ✅ Get **grid-ready data** for frontend tables

## 🚀 Quick Links

- **Swagger UI**: `http://localhost:8445/swagger-ui.html`
- **API Docs**: `http://localhost:8445/v3/api-docs`
- **Main Endpoint**: `POST /api/dynamic-records/query`

## 📋 API Endpoints

### Template Form Default (Columns) - **NEW**

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/template-forms-default/template/{id}` | Get column definitions (table headers) |
| `GET` | `/api/template-forms-default/column/{key}` | Get specific column by key |

### Dynamic Records

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/dynamic-records/query-simple` | **⭐ RECOMMENDED** Query records without columns |
| `GET` | `/api/dynamic-records/template/{id}` | Simple paginated query without columns |
| `POST` | `/api/dynamic-records/query` | ⚠️ DEPRECATED - Query with columns (use separate APIs) |

### Template Forms

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/template-forms/add` | Add fields to template |
| `GET` | `/api/template-forms?menuId={id}` | Get template fields |
| `DELETE` | `/api/template-forms/remove` | Remove fields |

### Template Form Values

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/template-forms-value/add` | Add values to form |

## 🏗️ Architecture Overview

```
┌─────────────┐
│   Client    │
│  (React/    │
│   Angular)  │
└──────┬──────┘
       │
       │ HTTP Request
       ▼
┌─────────────────────┐
│   Controller        │
│  (REST Endpoints)   │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   Service Layer     │
│  (Business Logic)   │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   Repository        │
│  (Data Access)      │
└──────┬──────────────┘
       │
       ▼
┌─────────────────────┐
│   PostgreSQL        │
│  (Database)         │
└─────────────────────┘
```

## 💾 Data Model

```
Template (User + Menu)
    │
    ├─> TemplateFormDefault (Column Definitions)
    │       │
    │       └─> TemplateFormValueDefault (Field Values)
    │                   │
    │                   └─> Record (Lead/Contact/Merchant)
    │
    └─> TemplateForm (Custom User Fields)
            │
            └─> TemplateFormValue (Custom Field Values)
```

## 🔧 Configuration

### Application Properties

```properties
# Swagger Configuration
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.operationsSorter=method
springdoc.swagger-ui.tagsSorter=alpha
```

### Security Configuration

Swagger endpoints are whitelisted in `SecurityConfiguration.java`:
- `/v3/api-docs/**`
- `/swagger-ui/**`
- `/swagger-ui.html`

## 📊 Example Request/Response

### Recommended Approach (Separated APIs)

**Step 1: Get Columns (Once)**
```bash
GET /api/template-forms-default/template/1
```

**Response:**
```json
[
  {
    "id": 1,
    "key": "name_abc",
    "label": "Full Name",
    "type": "TEXT",
    "priority": 1
  },
  {
    "id": 2,
    "key": "email_def",
    "label": "Email",
    "type": "EMAIL",
    "priority": 2
  }
]
```

**Step 2: Get Records (Multiple times)**
```bash
POST /api/dynamic-records/query-simple
```

```json
{
  "templateId": 1,
  "page": 0,
  "size": 10,
  "filters": [
    {
      "fieldKey": "status",
      "operator": "EQUALS",
      "value": "Active"
    }
  ]
}
```

**Response:**
```json
{
  "records": [
    {
      "recordId": 101,
      "fields": {
        "name_abc": "John Doe",
        "email_def": "john@example.com"
      }
    }
  ],
  "totalRecords": 50,
  "currentPage": 0,
  "pageSize": 10,
  "totalPages": 5
}
```

## 🎨 Frontend Integration

### React Example (Separated APIs)

```jsx
import { useState, useEffect } from 'react';

function DynamicGrid({ templateId }) {
  const [columns, setColumns] = useState([]);
  const [records, setRecords] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch columns once
    fetchColumns();
  }, [templateId]);

  useEffect(() => {
    // Fetch records when columns are loaded
    if (columns.length > 0) {
      fetchRecords();
    }
  }, [columns]);

  const fetchColumns = async () => {
    const res = await fetch(
      `/api/template-forms-default/template/${templateId}`,
      { headers: { 'Authorization': `Bearer ${token}` }}
    );
    const data = await res.json();
    setColumns(data.sort((a, b) => a.priority - b.priority));
  };

  const fetchRecords = async () => {
    const res = await fetch('/api/dynamic-records/query-simple', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ templateId, page: 0, size: 20 })
    });
    const data = await res.json();
    setRecords(data.records);
    setLoading(false);
  };

  if (loading) return <div>Loading...</div>;

  return (
    <table>
      <thead>
        <tr>
          {columns.map(col => (
            <th key={col.key}>{col.label}</th>
          ))}
        </tr>
      </thead>
      <tbody>
        {records.map(record => (
          <tr key={record.recordId}>
            {columns.map(col => (
              <td key={col.key}>{record.fields[col.key] || ''}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
}
```

See [Separated APIs Guide](./SeparatedAPIsGuide.md) for more examples with caching.

## 🧪 Testing

### Using Swagger UI

1. Navigate to `http://localhost:8445/swagger-ui.html`
2. Click on "Authorize" button
3. Enter your JWT token
4. Find the "Dynamic Records" section
5. Try the endpoints!

### Using cURL

```bash
# Simple query
curl -X GET "http://localhost:8445/api/dynamic-records/template/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_TOKEN"

# Advanced query with filters
curl -X POST "http://localhost:8445/api/dynamic-records/query" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "templateId": 1,
    "page": 0,
    "size": 10,
    "filters": [
      {
        "fieldKey": "status",
        "operator": "EQUALS",
        "value": "Active"
      }
    ]
  }'
```

## 📈 Performance

### Best Practices

1. **Pagination**: Always use reasonable page sizes (≤ 100)
2. **Filtering**: Use specific filters to reduce result sets
3. **Indexing**: Ensure database indexes are in place
4. **Caching**: Cache column definitions and common queries

### Database Indexes

```sql
-- Recommended indexes
CREATE INDEX idx_tfd_template_id ON template_form_default(template_id);
CREATE INDEX idx_tfvd_record_id ON template_form_value_default(record_id);
CREATE INDEX idx_tfvd_composite ON template_form_value_default(template_form_default_id, record_id);
```

## 🐛 Troubleshooting

### Common Issues

1. **401 Unauthorized**
   - Check JWT token validity
   - Ensure token is included in Authorization header

2. **404 Template Not Found**
   - Verify templateId exists
   - Check user has access to template

3. **Empty Results**
   - Verify records exist for template
   - Check filter conditions
   - Review query logs

4. **Slow Performance**
   - Add database indexes
   - Reduce page size
   - Optimize filters

## 📝 Version History

| Version | Date | Changes |
|---------|------|---------|
| 1.0.0 | 2025-11-06 | Initial release with Dynamic Records API |

## 🤝 Contributing

For questions or issues, please contact the development team.

---

**Last Updated:** 2025-11-06
