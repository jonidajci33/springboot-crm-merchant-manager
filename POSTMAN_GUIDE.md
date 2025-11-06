# Postman Collection Guide

## 📦 Collection File

**Location:** `Dynamic_Records_API.postman_collection.json`

This collection includes all the APIs created today for the Dynamic Records feature.

---

## 🚀 How to Import

### Step 1: Open Postman

### Step 2: Import Collection

**Option A: Drag and Drop**
1. Open Postman
2. Drag `Dynamic_Records_API.postman_collection.json` into Postman window
3. Done!

**Option B: Import Button**
1. Click "Import" button (top left)
2. Select "File"
3. Choose `Dynamic_Records_API.postman_collection.json`
4. Click "Import"

---

## ⚙️ Configuration

After importing, configure the variables:

### Step 1: Edit Collection Variables

1. Click on the collection name "Dynamic Records API - Merchant Manager"
2. Go to "Variables" tab
3. Update these values:

| Variable | Current Value | Description |
|----------|---------------|-------------|
| `baseUrl` | `http://localhost:8445` | Your API base URL |
| `token` | `your_jwt_token_here` | Your JWT Bearer token |
| `menuId` | `4` | Menu ID (default: 4) |
| `templateId` | `1` | Template ID (default: 1) |

### Step 2: Get Your JWT Token

1. Login to your application
2. Copy the JWT token from the response
3. Paste it into the `token` variable in Postman

**OR** use Postman to login:

```
POST {{baseUrl}}/api/v1/auth/login
{
    "username": "your_username",
    "password": "your_password"
}
```

Copy the token from the response and save it to the `token` variable.

---

## 📋 Collection Structure

The collection is organized into 3 folders:

### 1. **Template Form Default**
Management of system-wide column definitions.

| Request | Method | Description |
|---------|--------|-------------|
| Get Columns by Menu ID | GET | Get table headers/columns |
| Get Column by Key | GET | Get specific column |
| Add Fields to Default Template | POST | Add new columns (SUPERUSER only) |

### 2. **Dynamic Records**
Query dynamic records with filtering and pagination.

| Request | Method | Description |
|---------|--------|-------------|
| Get Records (Simple) | GET | Get paginated records |
| Query Records with Filters | POST | Advanced filtering & sorting |
| Filter by Status - Active | POST | Example: Active companies |
| Filter by Revenue | POST | Example: Revenue > $5M |
| Search by Company Name | POST | Example: Contains "Tech" |
| Multiple Filters | POST | Example: Active + High Revenue |
| Sort by Company Name | POST | Example: Alphabetical sort |

### 3. **Template Forms (User-Specific)**
User-customized template management.

| Request | Method | Description |
|---------|--------|-------------|
| Get Template Fields | GET | Get user's custom fields |
| Add Fields to User Template | POST | Add custom fields |
| Remove Fields from User Template | DELETE | Remove fields |

---

## 🧪 Testing Workflow

### Quick Test Sequence

1. **Get Columns** (Do this once)
   ```
   GET /api/template-forms-default/template/{{menuId}}
   ```
   - Cache these columns in your frontend
   - Use for building table headers

2. **Get All Records**
   ```
   GET /api/dynamic-records/template/{{menuId}}?page=0&size=10
   ```
   - Returns first 10 records
   - No column definitions (smaller payload)

3. **Filter Active Companies**
   ```
   POST /api/dynamic-records/query-simple
   Body: { filters: [{ fieldKey: "status_key_005", operator: "EQUALS", value: "active" }] }
   ```
   - Should return 6 active companies

4. **Search by Name**
   ```
   POST /api/dynamic-records/query-simple
   Body: { filters: [{ fieldKey: "company_name_key_001", operator: "CONTAINS", value: "Tech" }] }
   ```
   - Returns companies with "Tech" in name

---

## 🎯 Common Use Cases

### Use Case 1: Build a Data Grid

**Step 1:** Get columns
```
GET /api/template-forms-default/template/4
```

**Step 2:** Get records
```
GET /api/dynamic-records/template/4?page=0&size=20
```

**Step 3:** Map data to grid
```javascript
// Frontend pseudocode
const columns = step1Response.map(col => ({
  field: col.key,
  headerName: col.label,
  width: col.style?.width || 150
}));

const rows = step2Response.records.map(record => ({
  id: record.recordId,
  ...record.fields
}));
```

---

### Use Case 2: Implement Search

**Request:**
```
POST /api/dynamic-records/query-simple
{
  "menuId": 4,
  "page": 0,
  "size": 20,
  "filters": [
    {
      "fieldKey": "company_name_key_001",
      "operator": "CONTAINS",
      "value": "user_search_term"
    }
  ]
}
```

---

### Use Case 3: Filter by Dropdown

**Request:**
```
POST /api/dynamic-records/query-simple
{
  "menuId": 4,
  "filters": [
    {
      "fieldKey": "status_key_005",
      "operator": "EQUALS",
      "value": "active"
    }
  ]
}
```

---

### Use Case 4: Sort Results

**Request:**
```
POST /api/dynamic-records/query-simple
{
  "menuId": 4,
  "sortBy": "revenue_key_006",
  "sortDirection": "DESC"
}
```

---

## 🔍 Filter Operators Reference

| Operator | Description | Example |
|----------|-------------|---------|
| `EQUALS` | Exact match (case-insensitive) | `"operator": "EQUALS", "value": "active"` |
| `CONTAINS` | Contains substring | `"operator": "CONTAINS", "value": "Tech"` |
| `STARTS_WITH` | Starts with string | `"operator": "STARTS_WITH", "value": "Acme"` |
| `ENDS_WITH` | Ends with string | `"operator": "ENDS_WITH", "value": "Inc"` |
| `GREATER_THAN` | Numeric > | `"operator": "GREATER_THAN", "value": "1000000"` |
| `LESS_THAN` | Numeric < | `"operator": "LESS_THAN", "value": "5000000"` |

---

## 📊 Expected Results (With Sample Data)

### Total Records: 10 companies

### By Status:
- **Active:** 6 companies
- **Pending:** 2 companies
- **Inactive:** 2 companies

### Sample Companies:
1. Acme Corporation - Active - $5M
2. TechStart Inc - Pending - $2.5M
3. Global Solutions Ltd - Active - $8.75M
4. Innovative Designs Co - Active - $3.2M
5. NextGen Industries - Inactive - $1.5M
6. Prime Services Group - Active - $6.5M
7. Digital Dynamics - Pending - $4.1M
8. Enterprise Solutions Inc - Inactive - $12M
9. CloudTech Systems - Active - $7.8M
10. Future Ventures LLC - Active - $9.2M

---

## 🐛 Troubleshooting

### Issue: 401 Unauthorized

**Solution:** Update your JWT token
1. Login via API or UI
2. Copy the new token
3. Update `token` variable in Postman

---

### Issue: 404 Template Not Found

**Solution:** Check your menuId
1. Ensure Menu ID 4 exists
2. Ensure Template ID 1 exists
3. Run the sample data SQL script

---

### Issue: Empty Response

**Solution:** Verify data exists
```sql
-- Check if data exists
SELECT COUNT(*) FROM template_form_default WHERE template_id = 1;
SELECT COUNT(*) FROM lead;
SELECT COUNT(*) FROM template_form_value_default;
```

---

## 💡 Tips

1. **Save Token:** After getting your JWT, save it to the collection variable for reuse
2. **Use Environments:** Create different environments (dev, staging, prod)
3. **Pre-request Scripts:** Add automatic token refresh if needed
4. **Tests:** Add assertions to validate responses
5. **Examples:** Save successful responses as examples for documentation

---

## 🔗 Related Documentation

- [Separated APIs Guide](Documentation/SeparatedAPIsGuide.md)
- [Quick Start Guide](Documentation/QuickStartGuide.md)
- [Architecture Documentation](Documentation/DynamicRecordsArchitecture.md)
- [Sample Data Setup](src/main/resources/README-SampleData.md)

---

## 📝 Example Response Structures

### Get Columns Response
```json
[
  {
    "id": 1,
    "key": "company_name_key_001",
    "label": "Company Name",
    "type": "TEXT",
    "priority": 1,
    "style": { "width": "200px" }
  }
]
```

### Get Records Response
```json
{
  "records": [
    {
      "recordId": 1,
      "fields": {
        "company_name_key_001": "Acme Corporation",
        "email_key_003": "john.smith@acme.com",
        "status_key_005": "active"
      }
    }
  ],
  "totalRecords": 10,
  "currentPage": 0,
  "pageSize": 10,
  "totalPages": 1
}
```

---

**Happy Testing! 🎉**
