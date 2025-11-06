# Sample Data Setup Guide

This guide helps you insert sample data to test the Dynamic Records API.

## 📋 Prerequisites

This sample data is configured for:
- **User ID: 3**
- **Menu ID: 4**
- **Template ID: 1**

Before inserting sample data, you need these to exist in your database.

## 🔍 Step 1: Verify Setup

Run the verification script to check if you have the required data:

```bash
# Using psql
psql -U postgres -d aegis_broker -f verify-setup.sql
```

This will show:
- ✅ User ID 3 exists
- ✅ Menu ID 4 exists
- ✅ Template ID 1 exists
- ✅ Template links User 3 and Menu 4

## 🔧 Step 2: Create Missing Data (If Needed)

If verification shows missing data, run:

```bash
psql -U postgres -d aegis_broker -f create-missing-data.sql
```

This will:
- Create Menu ID 4 if it doesn't exist
- Create Template ID 1 linking User 3 and Menu 4
- Verify everything is set up correctly

**Note:** If User ID 3 doesn't exist, you need to create it through your application's registration endpoint or manually with a proper password hash.

## 📝 Step 3: Sample Data Script (Already Configured)

The `sample-data.sql` script is already configured for:
- Template ID: 1
- User ID: 3 (via template)
- Menu ID: 4 (via template)

No changes needed unless you want to use different IDs.

## 🚀 Step 4: Run the Sample Data Script

```sql
-- Using psql
psql -U postgres -d aegis_broker -f sample-data.sql

-- Or using pgAdmin/DBeaver:
-- Copy the contents of sample-data.sql and execute
```

## ✅ Step 5: Verify the Data

Run these verification queries:

```sql
-- Check column definitions
SELECT * FROM template_form_default
WHERE template_id = 1
ORDER BY priority;

-- Check lead records
SELECT * FROM lead ORDER BY id LIMIT 10;

-- Check values for record 1
SELECT tfd.label, tfvd.value
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfvd.record_id = 1
ORDER BY tfd.priority;

-- Check records by status
SELECT tfvd.value as status, COUNT(DISTINCT tfvd.record_id) as count
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfd.key = 'status_key_005'
GROUP BY tfvd.value;
```

Expected results:
- **8 columns** in `template_form_default`
- **10 leads** in `lead` table
- **80 values** in `template_form_value_default` (10 records × 8 fields)

## 🧪 Step 6: Test the API

### Get Columns (Table Headers)
```bash
curl -X GET "http://localhost:8445/api/template-forms-default/template/1" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get Records (Paginated)
```bash
curl -X GET "http://localhost:8445/api/dynamic-records/template/1?page=0&size=10" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Query with Filters
```bash
curl -X POST "http://localhost:8445/api/dynamic-records/query-simple" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "menuId": 1,
    "page": 0,
    "size": 10,
    "filters": [
      {
        "fieldKey": "status_key_005",
        "operator": "EQUALS",
        "value": "active"
      }
    ]
  }'
```

## 📊 Sample Data Overview

The script creates:

### Columns (8 fields):
1. **Company Name** - TEXT
2. **Contact Person** - TEXT
3. **Email Address** - EMAIL
4. **Phone Number** - TEXT
5. **Status** - SELECT (active/inactive/pending)
6. **Annual Revenue** - NUMBER
7. **Industry** - TEXT
8. **Location** - TEXT

### Records (10 companies):
1. Acme Corporation - Active, Tech, New York
2. TechStart Inc - Pending, Software, San Francisco
3. Global Solutions Ltd - Active, Consulting, Chicago
4. Innovative Designs Co - Active, Design, Los Angeles
5. NextGen Industries - Inactive, Manufacturing, Detroit
6. Prime Services Group - Active, Services, Houston
7. Digital Dynamics - Pending, Marketing, Miami
8. Enterprise Solutions Inc - Inactive, Enterprise Software, Seattle
9. CloudTech Systems - Active, Cloud Computing, Austin
10. Future Ventures LLC - Active, Venture Capital, Boston

### Filtering Examples:
- **Active records**: 6 companies
- **Pending records**: 2 companies
- **Inactive records**: 2 companies

## 🔄 Reset Data (Optional)

If you want to start over:

```sql
-- Delete all sample data
DELETE FROM template_form_value_default WHERE record_id BETWEEN 1 AND 10;
DELETE FROM lead WHERE id BETWEEN 1 AND 10;
DELETE FROM template_form_default WHERE template_id = 1;
```

## 🐛 Troubleshooting

### Issue: Foreign key constraint error
**Solution**: Make sure the template ID exists and matches your actual data.

```sql
-- Check if template exists
SELECT * FROM template WHERE id = 1;
```

### Issue: Duplicate key error
**Solution**: The keys already exist. Either:
1. Delete existing data first
2. Change the keys in the sample data script

### Issue: No data returned from API
**Solution**: Check:
1. Template ID matches your menu ID
2. User has access to the template
3. Records are linked correctly

```sql
-- Verify linkage
SELECT t.id as template_id, tfd.id as column_id, tfd.label,
       tfvd.record_id, tfvd.value
FROM template t
JOIN template_form_default tfd ON t.id = tfd.template_id
LEFT JOIN template_form_value_default tfvd ON tfd.id = tfvd.template_form_default_id
WHERE t.id = 1
LIMIT 20;
```

## 📚 Next Steps

After inserting the sample data:
1. Test the Swagger UI at `http://localhost:8445/swagger-ui.html`
2. Build a frontend grid using the columns and records
3. Try different filters and pagination
4. Test sorting by different fields

Happy testing! 🎉
