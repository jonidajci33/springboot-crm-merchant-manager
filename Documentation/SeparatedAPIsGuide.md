# Separated APIs Guide - Columns & Records

## 🎯 Why Separate APIs?

The API has been split into two endpoints for better performance and flexibility:

1. **Columns API** - Get table headers/column definitions (rarely changes)
2. **Records API** - Get paginated data (changes frequently)

### Benefits:
✅ **Better Performance** - Don't send column metadata with every paginated request
✅ **Caching** - Frontend can cache column definitions
✅ **Reduced Payload** - Smaller response size for record queries
✅ **Flexibility** - Fetch columns once, fetch records multiple times

---

## 📋 API Endpoints

### 1. Get Columns (Table Headers)

**Endpoint:**
```
GET /api/template-forms-default/template/{templateId}
```

**Purpose:** Get column definitions for building table headers

**Response:**
```json
[
  {
    "id": 1,
    "key": "name_abc123",
    "label": "Full Name",
    "type": "TEXT",
    "priority": 1,
    "options": null,
    "style": {"width": "200px"}
  },
  {
    "id": 2,
    "key": "email_def456",
    "label": "Email Address",
    "type": "EMAIL",
    "priority": 2,
    "options": null,
    "style": null
  }
]
```

### 2. Get Records (Table Rows)

**Endpoint:**
```
POST /api/dynamic-records/query-simple
```

**Request:**
```json
{
  "templateId": 1,
  "page": 0,
  "size": 20,
  "sortBy": "name_abc123",
  "sortDirection": "ASC",
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
      "recordId": 501,
      "fields": {
        "name_abc123": "John Doe",
        "email_def456": "john@example.com"
      }
    }
  ],
  "totalRecords": 50,
  "currentPage": 0,
  "pageSize": 20,
  "totalPages": 3
}
```

---

## 🚀 Frontend Implementation

### React/TypeScript Example

```typescript
import { useState, useEffect } from 'react';
import { DataGrid, GridColDef } from '@mui/x-data-grid';

interface Column {
  id: number;
  key: string;
  label: string;
  type: string;
  priority: number;
  style?: Record<string, string>;
}

interface Record {
  recordId: number;
  fields: Record<string, string>;
}

function LeadGrid({ templateId }: { templateId: number }) {
  const [columns, setColumns] = useState<GridColDef[]>([]);
  const [rows, setRows] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [pagination, setPagination] = useState({
    page: 0,
    pageSize: 20,
    totalRecords: 0
  });

  // Step 1: Fetch columns ONCE (cache this!)
  useEffect(() => {
    fetchColumns();
  }, [templateId]);

  // Step 2: Fetch records on pagination change
  useEffect(() => {
    if (columns.length > 0) {
      fetchRecords();
    }
  }, [columns, pagination.page, pagination.pageSize]);

  const fetchColumns = async () => {
    try {
      const response = await fetch(
        `/api/template-forms-default/template/${templateId}`,
        {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        }
      );

      const columnsData: Column[] = await response.json();

      // Build DataGrid columns
      const gridColumns: GridColDef[] = columnsData
        .sort((a, b) => a.priority - b.priority)
        .map(col => ({
          field: col.key,
          headerName: col.label,
          width: col.style?.width ? parseInt(col.style.width) : 150,
          sortable: true,
          flex: 1
        }));

      setColumns(gridColumns);
    } catch (error) {
      console.error('Error fetching columns:', error);
    }
  };

  const fetchRecords = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/dynamic-records/query-simple', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          templateId,
          page: pagination.page,
          size: pagination.pageSize
        })
      });

      const data = await response.json();

      // Build rows
      const gridRows = data.records.map((record: Record) => ({
        id: record.recordId,
        ...record.fields
      }));

      setRows(gridRows);
      setPagination(prev => ({
        ...prev,
        totalRecords: data.totalRecords
      }));
    } catch (error) {
      console.error('Error fetching records:', error);
    } finally {
      setLoading(false);
    }
  };

  const handlePageChange = (newPage: number) => {
    setPagination(prev => ({ ...prev, page: newPage }));
  };

  return (
    <div style={{ height: 600, width: '100%' }}>
      <DataGrid
        rows={rows}
        columns={columns}
        loading={loading}
        paginationMode="server"
        rowCount={pagination.totalRecords}
        page={pagination.page}
        pageSize={pagination.pageSize}
        onPageChange={handlePageChange}
        onPageSizeChange={(newSize) =>
          setPagination(prev => ({ ...prev, pageSize: newSize, page: 0 }))
        }
        rowsPerPageOptions={[10, 20, 50, 100]}
      />
    </div>
  );
}
```

### With Caching (React Query)

```typescript
import { useQuery } from '@tanstack/react-query';

// Cache columns for 1 hour (they rarely change)
const useColumns = (templateId: number) => {
  return useQuery({
    queryKey: ['columns', templateId],
    queryFn: async () => {
      const response = await fetch(
        `/api/template-forms-default/template/${templateId}`,
        {
          headers: { 'Authorization': `Bearer ${token}` }
        }
      );
      return response.json();
    },
    staleTime: 60 * 60 * 1000, // 1 hour
    cacheTime: 60 * 60 * 1000
  });
};

// Fetch records (refetch frequently)
const useRecords = (templateId: number, page: number, size: number) => {
  return useQuery({
    queryKey: ['records', templateId, page, size],
    queryFn: async () => {
      const response = await fetch('/api/dynamic-records/query-simple', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ templateId, page, size })
      });
      return response.json();
    },
    staleTime: 0, // Always refetch
    keepPreviousData: true // Smooth pagination
  });
};

// Usage
function LeadGrid({ templateId }: { templateId: number }) {
  const [page, setPage] = useState(0);
  const { data: columns } = useColumns(templateId);
  const { data: records, isLoading } = useRecords(templateId, page, 20);

  // Build grid...
}
```

### Vanilla JavaScript

```javascript
class DynamicGrid {
  constructor(containerId, templateId) {
    this.container = document.getElementById(containerId);
    this.templateId = templateId;
    this.columns = [];
    this.page = 0;
    this.pageSize = 20;
  }

  async init() {
    // Step 1: Fetch and cache columns
    await this.fetchColumns();

    // Step 2: Fetch initial records
    await this.fetchRecords();

    // Step 3: Render grid
    this.render();
  }

  async fetchColumns() {
    // Check localStorage cache first
    const cached = localStorage.getItem(`columns_${this.templateId}`);
    if (cached) {
      this.columns = JSON.parse(cached);
      return;
    }

    const response = await fetch(
      `/api/template-forms-default/template/${this.templateId}`,
      {
        headers: { 'Authorization': `Bearer ${token}` }
      }
    );

    this.columns = await response.json();

    // Cache for 1 hour
    localStorage.setItem(`columns_${this.templateId}`, JSON.stringify(this.columns));
  }

  async fetchRecords() {
    const response = await fetch('/api/dynamic-records/query-simple', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        templateId: this.templateId,
        page: this.page,
        size: this.pageSize
      })
    });

    this.data = await response.json();
  }

  render() {
    // Sort columns by priority
    const sortedColumns = this.columns.sort((a, b) => a.priority - b.priority);

    let html = '<table class="dynamic-grid">';

    // Build header
    html += '<thead><tr>';
    sortedColumns.forEach(col => {
      html += `<th>${col.label}</th>`;
    });
    html += '</tr></thead>';

    // Build rows
    html += '<tbody>';
    this.data.records.forEach(record => {
      html += '<tr>';
      sortedColumns.forEach(col => {
        const value = record.fields[col.key] || '';
        html += `<td>${value}</td>`;
      });
      html += '</tr>';
    });
    html += '</tbody>';

    html += '</table>';

    this.container.innerHTML = html;
  }
}

// Usage
const grid = new DynamicGrid('leadGrid', 1);
grid.init();
```

---

## 🔄 Request Flow

```
┌─────────────────────────────────────────────────────────┐
│                  FRONTEND APPLICATION                   │
└─────────────────────────────────────────────────────────┘
                        │
        ┌───────────────┴───────────────┐
        │                               │
        ▼                               ▼
┌──────────────────┐         ┌──────────────────────┐
│  1. GET Columns  │         │  2. POST Records     │
│  (ONCE/Cached)   │         │  (Every pagination)  │
└────────┬─────────┘         └──────────┬───────────┘
         │                               │
         ▼                               ▼
┌──────────────────┐         ┌──────────────────────┐
│ TemplateForm     │         │ DynamicRecord        │
│ DefaultController│         │ Controller           │
└────────┬─────────┘         └──────────┬───────────┘
         │                               │
         ▼                               ▼
┌──────────────────┐         ┌──────────────────────┐
│ TemplateForm     │         │ DynamicRecord        │
│ DefaultService   │         │ Service              │
└────────┬─────────┘         └──────────┬───────────┘
         │                               │
         ▼                               ▼
┌──────────────────┐         ┌──────────────────────┐
│ TemplateForm     │         │ TemplateFormValue    │
│ Default          │         │ Default              │
│ Repository       │         │ Repository           │
└────────┬─────────┘         └──────────┬───────────┘
         │                               │
         ▼                               ▼
┌─────────────────────────────────────────────────────┐
│              PostgreSQL Database                     │
└─────────────────────────────────────────────────────┘

RESPONSE:
┌──────────────────┐         ┌──────────────────────┐
│ Columns          │         │ Records Only         │
│ [                │         │ {                    │
│   {              │         │   "records": [...],  │
│     "label":..., │         │   "totalRecords": 50,│
│     "key": ...,  │         │   "currentPage": 0,  │
│     "type": ...  │         │   "pageSize": 20,    │
│   }              │         │   "totalPages": 3    │
│ ]                │         │ }                    │
└──────────────────┘         └──────────────────────┘
        │                               │
        └───────────────┬───────────────┘
                        ▼
            ┌────────────────────────┐
            │   Build Complete Grid  │
            │   Headers + Rows       │
            └────────────────────────┘
```

---

## 📊 Performance Comparison

### Old API (Combined)

```
Request 1: 500KB (columns + records)
Request 2: 500KB (columns + records) ← Redundant column data
Request 3: 500KB (columns + records) ← Redundant column data
Total: 1.5MB
```

### New API (Separated)

```
Request 1: 50KB (columns only)
Request 2: 400KB (records only)
Request 3: 400KB (records only)
Total: 850KB (43% reduction)
```

---

## 🎨 Complete Example (React + Material-UI)

```typescript
import { useState, useEffect } from 'react';
import { DataGrid, GridColDef, GridFilterModel } from '@mui/x-data-grid';
import { Button, CircularProgress } from '@mui/material';

function DynamicLeadGrid() {
  const templateId = 1;
  const [columns, setColumns] = useState<GridColDef[]>([]);
  const [rows, setRows] = useState<any[]>([]);
  const [loading, setLoading] = useState(false);
  const [pagination, setPagination] = useState({
    page: 0,
    pageSize: 20,
    totalRows: 0
  });
  const [filters, setFilters] = useState([]);

  // Fetch columns once on mount
  useEffect(() => {
    loadColumns();
  }, []);

  // Fetch records when page/filters change
  useEffect(() => {
    if (columns.length > 0) {
      loadRecords();
    }
  }, [pagination.page, pagination.pageSize, filters, columns]);

  const loadColumns = async () => {
    try {
      const response = await fetch(
        `/api/template-forms-default/template/${templateId}`,
        {
          headers: { 'Authorization': `Bearer ${getToken()}` }
        }
      );

      const columnsData = await response.json();

      const gridColumns: GridColDef[] = columnsData
        .sort((a, b) => a.priority - b.priority)
        .map(col => ({
          field: col.key,
          headerName: col.label,
          width: 150,
          editable: false,
          sortable: true
        }));

      setColumns(gridColumns);
    } catch (error) {
      console.error('Failed to load columns:', error);
    }
  };

  const loadRecords = async () => {
    setLoading(true);
    try {
      const response = await fetch('/api/dynamic-records/query-simple', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${getToken()}`
        },
        body: JSON.stringify({
          templateId,
          page: pagination.page,
          size: pagination.pageSize,
          filters: filters
        })
      });

      const data = await response.json();

      const gridRows = data.records.map(record => ({
        id: record.recordId,
        ...record.fields
      }));

      setRows(gridRows);
      setPagination(prev => ({
        ...prev,
        totalRows: data.totalRecords
      }));
    } catch (error) {
      console.error('Failed to load records:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleFilter = (fieldKey: string, operator: string, value: string) => {
    setFilters([{ fieldKey, operator, value }]);
    setPagination(prev => ({ ...prev, page: 0 })); // Reset to first page
  };

  return (
    <div style={{ height: 700, width: '100%' }}>
      {columns.length === 0 ? (
        <CircularProgress />
      ) : (
        <>
          <div style={{ marginBottom: 16 }}>
            <Button onClick={() => handleFilter('status', 'EQUALS', 'Active')}>
              Show Active Only
            </Button>
            <Button onClick={() => setFilters([])}>
              Clear Filters
            </Button>
          </div>

          <DataGrid
            rows={rows}
            columns={columns}
            loading={loading}
            paginationMode="server"
            rowCount={pagination.totalRows}
            page={pagination.page}
            pageSize={pagination.pageSize}
            onPageChange={(newPage) =>
              setPagination(prev => ({ ...prev, page: newPage }))
            }
            onPageSizeChange={(newSize) =>
              setPagination(prev => ({ ...prev, pageSize: newSize, page: 0 }))
            }
            rowsPerPageOptions={[10, 20, 50, 100]}
            checkboxSelection
            disableSelectionOnClick
          />
        </>
      )}
    </div>
  );
}

function getToken() {
  return localStorage.getItem('auth_token') || '';
}
```

---

## ✅ Best Practices

1. **Cache Columns**: Store column definitions in localStorage or state management
2. **Refetch Records**: Always get fresh data on pagination/filter changes
3. **Error Handling**: Handle both API failures gracefully
4. **Loading States**: Show loading indicators for both columns and records
5. **Optimistic Updates**: Use previous data while fetching new data

---

## 🔧 Migration from Old API

If you're using the old combined API (`/api/dynamic-records/query`), here's how to migrate:

### Before (Old API):
```typescript
const response = await fetch('/api/dynamic-records/query', {
  method: 'POST',
  body: JSON.stringify({ templateId, page, size })
});

const data = await response.json();
const columns = data.columns;  // ← Sent every time
const records = data.records;
```

### After (New API):
```typescript
// Fetch columns once
const columnsResponse = await fetch(
  `/api/template-forms-default/template/${templateId}`
);
const columns = await columnsResponse.json();

// Fetch records separately
const recordsResponse = await fetch('/api/dynamic-records/query-simple', {
  method: 'POST',
  body: JSON.stringify({ templateId, page, size })
});
const data = await recordsResponse.json();
const records = data.records;
```

---

**Document Version:** 1.0
**Last Updated:** 2025-11-06
