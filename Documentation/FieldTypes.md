# Field Types Reference

## Available Field Types

The system supports the following field types for dynamic columns:

| Field Type | Description | Use Case | Example |
|------------|-------------|----------|---------|
| `TEXT` | Single-line text input | Names, titles, short text | Company Name, Email |
| `PHONE_NUMBER` | Phone number input | Phone numbers with formatting | (555) 123-4567 |
| `TEXT_BOX` | Multi-line text area | Long descriptions, notes | Company Description |
| `NUMBER` | Numeric input | Quantities, amounts, IDs | Revenue, Employee Count |
| `DROPDOWN` | Single selection dropdown | Status, category selection | Status: Active/Inactive |
| `MULTISELECTION` | Multiple selection | Tags, categories | Technologies: Java, Python, React |
| `CHECKBOX` | Boolean checkbox | Yes/No, True/False | Is Active, Is Verified |
| `RADIOBUTTON` | Radio button group | Single choice from options | Priority: Low/Medium/High |

---

## Field Type Details

### 1. TEXT
**Description:** Basic single-line text input field.

**Properties:**
- No special validation
- Supports any character
- Good for short text inputs

**Example Usage:**
```json
{
  "label": "Company Name",
  "type": "TEXT",
  "priority": 1
}
```

**Frontend Rendering:**
```html
<input type="text" />
```

---

### 2. PHONE_NUMBER
**Description:** Specialized input for phone numbers.

**Properties:**
- Can include formatting
- May include validation for phone number format
- Supports international formats

**Example Usage:**
```json
{
  "label": "Phone Number",
  "type": "PHONE_NUMBER",
  "priority": 4
}
```

**Frontend Rendering:**
```html
<input type="tel" />
```

**Value Examples:**
- `555-0101`
- `(555) 123-4567`
- `+1-555-123-4567`

---

### 3. TEXT_BOX
**Description:** Multi-line text area for longer content.

**Properties:**
- Allows line breaks
- Good for descriptions, notes, comments
- Can set rows/cols in style

**Example Usage:**
```json
{
  "label": "Company Description",
  "type": "TEXT_BOX",
  "priority": 10,
  "style": {
    "rows": "5",
    "cols": "50"
  }
}
```

**Frontend Rendering:**
```html
<textarea rows="5" cols="50"></textarea>
```

---

### 4. NUMBER
**Description:** Numeric input field.

**Properties:**
- Only accepts numbers
- Supports integers and decimals
- Can set min/max in options

**Example Usage:**
```json
{
  "label": "Annual Revenue",
  "type": "NUMBER",
  "priority": 6,
  "options": {
    "min": "0",
    "max": "999999999",
    "step": "1000"
  }
}
```

**Frontend Rendering:**
```html
<input type="number" min="0" step="1000" />
```

**Value Examples:**
- `5000000`
- `3.14`
- `100`

---

### 5. DROPDOWN
**Description:** Single selection dropdown menu.

**Properties:**
- Requires `options` object
- User can select one value
- Good for status, category fields

**Example Usage:**
```json
{
  "label": "Status",
  "type": "DROPDOWN",
  "priority": 5,
  "options": {
    "active": "Active",
    "inactive": "Inactive",
    "pending": "Pending"
  }
}
```

**Frontend Rendering:**
```html
<select>
  <option value="active">Active</option>
  <option value="inactive">Inactive</option>
  <option value="pending">Pending</option>
</select>
```

**Stored Value:** The key (e.g., `"active"`)

**Display Value:** The label (e.g., `"Active"`)

---

### 6. MULTISELECTION
**Description:** Multiple selection field (multi-select dropdown or checkbox group).

**Properties:**
- Requires `options` object
- User can select multiple values
- Values stored as array or comma-separated

**Example Usage:**
```json
{
  "label": "Technologies",
  "type": "MULTISELECTION",
  "priority": 9,
  "options": {
    "java": "Java",
    "python": "Python",
    "react": "React",
    "nodejs": "Node.js"
  }
}
```

**Frontend Rendering:**
```html
<select multiple>
  <option value="java">Java</option>
  <option value="python">Python</option>
  <option value="react">React</option>
  <option value="nodejs">Node.js</option>
</select>
```

**Stored Value:** `["java", "python", "react"]` or `"java,python,react"`

---

### 7. CHECKBOX
**Description:** Single checkbox for boolean values.

**Properties:**
- True/False or Yes/No
- No options needed
- Good for flags

**Example Usage:**
```json
{
  "label": "Is Active",
  "type": "CHECKBOX",
  "priority": 3
}
```

**Frontend Rendering:**
```html
<input type="checkbox" />
```

**Stored Value:**
- `true` or `"true"` when checked
- `false` or `"false"` when unchecked

---

### 8. RADIOBUTTON
**Description:** Radio button group for single selection.

**Properties:**
- Requires `options` object
- Only one can be selected
- Good for mutually exclusive choices

**Example Usage:**
```json
{
  "label": "Priority Level",
  "type": "RADIOBUTTON",
  "priority": 8,
  "options": {
    "low": "Low",
    "medium": "Medium",
    "high": "High"
  }
}
```

**Frontend Rendering:**
```html
<label><input type="radio" name="priority" value="low" /> Low</label>
<label><input type="radio" name="priority" value="medium" /> Medium</label>
<label><input type="radio" name="priority" value="high" /> High</label>
```

**Stored Value:** The selected key (e.g., `"medium"`)

---

## Adding Columns via API

### Using TEXT Field
```bash
POST /api/template-forms-default/add?menuId=4
{
  "label": "Company Website",
  "type": "TEXT",
  "priority": 9
}
```

### Using DROPDOWN Field
```bash
POST /api/template-forms-default/add?menuId=4
{
  "label": "Lead Source",
  "type": "DROPDOWN",
  "priority": 10,
  "options": {
    "website": "Website",
    "referral": "Referral",
    "cold_call": "Cold Call",
    "social_media": "Social Media"
  }
}
```

### Using MULTISELECTION Field
```bash
POST /api/template-forms-default/add?menuId=4
{
  "label": "Interested Products",
  "type": "MULTISELECTION",
  "priority": 11,
  "options": {
    "product_a": "Product A",
    "product_b": "Product B",
    "product_c": "Product C"
  }
}
```

### Using CHECKBOX Field
```bash
POST /api/template-forms-default/add?menuId=4
{
  "label": "Newsletter Subscription",
  "type": "CHECKBOX",
  "priority": 12
}
```

---

## Sample Data Uses These Types

Our sample data includes:

| Column | Type | Options |
|--------|------|---------|
| Company Name | TEXT | - |
| Contact Person | TEXT | - |
| Email Address | TEXT | - |
| Phone Number | PHONE_NUMBER | - |
| Status | DROPDOWN | Active, Inactive, Pending |
| Annual Revenue | NUMBER | - |
| Industry | DROPDOWN | 10 industry options |
| Location | TEXT | - |

---

## Frontend Rendering Guide

### React Example

```typescript
const renderField = (column: TemplateFormDefault, value: string) => {
  switch (column.type) {
    case 'TEXT':
    case 'PHONE_NUMBER':
      return <input type="text" value={value} />;

    case 'TEXT_BOX':
      return <textarea value={value} />;

    case 'NUMBER':
      return <input type="number" value={value} />;

    case 'DROPDOWN':
      return (
        <select value={value}>
          {Object.entries(column.options || {}).map(([key, label]) => (
            <option key={key} value={key}>{label}</option>
          ))}
        </select>
      );

    case 'MULTISELECTION':
      const selectedValues = value?.split(',') || [];
      return (
        <select multiple value={selectedValues}>
          {Object.entries(column.options || {}).map(([key, label]) => (
            <option key={key} value={key}>{label}</option>
          ))}
        </select>
      );

    case 'CHECKBOX':
      return <input type="checkbox" checked={value === 'true'} />;

    case 'RADIOBUTTON':
      return Object.entries(column.options || {}).map(([key, label]) => (
        <label key={key}>
          <input type="radio" value={key} checked={value === key} />
          {label}
        </label>
      ));

    default:
      return <input type="text" value={value} />;
  }
};
```

---

## Validation Rules

| Type | Validation |
|------|------------|
| TEXT | Max length (optional) |
| PHONE_NUMBER | Phone format (optional) |
| TEXT_BOX | Max length (optional) |
| NUMBER | Min/max values, numeric only |
| DROPDOWN | Must be one of the options |
| MULTISELECTION | Must be subset of options |
| CHECKBOX | Boolean only |
| RADIOBUTTON | Must be one of the options |

---

**Last Updated:** 2025-11-06
