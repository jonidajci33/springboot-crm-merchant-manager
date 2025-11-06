-- ============================================================
-- COMPLETE SAMPLE DATA SCRIPT
-- ============================================================
-- Configuration:
--   - User ID: 3 (must exist)
--   - Menu ID: 4
--   - Template ID: 1
--
-- This script creates:
--   - Menu ID 4 (Leads)
--   - Template ID 1 (User 3 + Menu 4)
--   - 8 Column Definitions
--   - 10 Lead Records
--   - 80 Field Values
--
-- Instructions:
--   1. Make sure User ID 3 exists in your database
--   2. Copy this entire file
--   3. Paste into your SQL editor
--   4. Execute all queries
-- ============================================================

-- Clean up existing sample data (optional - uncomment if you want to reset)
-- DELETE FROM template_form_value_default WHERE template_form_default_id IN (SELECT id FROM template_form_default WHERE template_id = 1);
-- DELETE FROM template_form_default WHERE template_id = 1;
-- DELETE FROM lead WHERE id BETWEEN 1 AND 10;
-- DELETE FROM template WHERE id = 1;
-- DELETE FROM menu WHERE id = 4;

-- ============================================================
-- STEP 1: Create Menu ID 4
-- ============================================================
INSERT INTO menu (id, name, created_at, updated_at, created_by, last_updated_by)
VALUES (4, 'Leads', NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- STEP 2: Create Template ID 1 (User 3 + Menu 4)
-- ============================================================
INSERT INTO template (id, user_id, menu_id, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 3, 4, NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- STEP 3: Insert Column Definitions (TemplateFormDefault)
-- ============================================================

-- Column 1: Company Name
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'company_name_key_001', 'Company Name', 'TEXT', 1, NULL, '{"width": "200px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 2: Contact Person
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'contact_person_key_002', 'Contact Person', 'TEXT', 2, NULL, '{"width": "180px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 3: Email
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'email_key_003', 'Email Address', 'TEXT', 3, NULL, '{"width": "200px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 4: Phone
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'phone_key_004', 'Phone Number', 'PHONE_NUMBER', 4, NULL, '{"width": "150px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 5: Status
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'status_key_005', 'Status', 'DROPDOWN', 5, '{"active": "Active", "inactive": "Inactive", "pending": "Pending"}'::jsonb, '{"width": "120px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 6: Revenue
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'revenue_key_006', 'Annual Revenue', 'NUMBER', 6, NULL, '{"width": "150px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 7: Industry
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'industry_key_007', 'Industry', 'DROPDOWN', 7, '{"technology": "Technology", "software": "Software", "consulting": "Consulting", "design": "Design", "manufacturing": "Manufacturing", "services": "Services", "marketing": "Marketing", "enterprise_software": "Enterprise Software", "cloud_computing": "Cloud Computing", "venture_capital": "Venture Capital"}'::jsonb, '{"width": "150px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- Column 8: Location
INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 'location_key_008', 'Location', 'TEXT', 8, NULL, '{"width": "180px"}'::jsonb, NOW(), NOW(), 'system', 'system');

-- ============================================================
-- STEP 4: Insert Lead Records
-- ============================================================

INSERT INTO lead (is_signed, is_active, created_at, updated_at, created_by, last_updated_by) VALUES
(true, true, NOW(), NOW(), 'system', 'system'),    -- Lead ID: 1
(false, true, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 2
(false, true, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 3
(true, true, NOW(), NOW(), 'system', 'system'),    -- Lead ID: 4
(false, true, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 5
(true, true, NOW(), NOW(), 'system', 'system'),    -- Lead ID: 6
(false, true, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 7
(true, false, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 8
(false, true, NOW(), NOW(), 'system', 'system'),   -- Lead ID: 9
(true, true, NOW(), NOW(), 'system', 'system');    -- Lead ID: 10

-- ============================================================
-- STEP 5: Insert Values for Lead 1 - Acme Corporation
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 1, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Acme Corporation'),
  ('contact_person_key_002', 'John Smith'),
  ('email_key_003', 'john.smith@acme.com'),
  ('phone_key_004', '555-0101'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '5000000'),
  ('industry_key_007', 'Technology'),
  ('location_key_008', 'New York, NY')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 6: Insert Values for Lead 2 - TechStart Inc
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 2, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'TechStart Inc'),
  ('contact_person_key_002', 'Sarah Johnson'),
  ('email_key_003', 'sarah@techstart.com'),
  ('phone_key_004', '555-0102'),
  ('status_key_005', 'pending'),
  ('revenue_key_006', '2500000'),
  ('industry_key_007', 'Software'),
  ('location_key_008', 'San Francisco, CA')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 7: Insert Values for Lead 3 - Global Solutions Ltd
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 3, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Global Solutions Ltd'),
  ('contact_person_key_002', 'Michael Brown'),
  ('email_key_003', 'mbrown@globalsolutions.com'),
  ('phone_key_004', '555-0103'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '8750000'),
  ('industry_key_007', 'Consulting'),
  ('location_key_008', 'Chicago, IL')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 8: Insert Values for Lead 4 - Innovative Designs Co
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 4, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Innovative Designs Co'),
  ('contact_person_key_002', 'Emily Davis'),
  ('email_key_003', 'emily@innovativedesigns.com'),
  ('phone_key_004', '555-0104'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '3200000'),
  ('industry_key_007', 'Design'),
  ('location_key_008', 'Los Angeles, CA')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 9: Insert Values for Lead 5 - NextGen Industries
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 5, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'NextGen Industries'),
  ('contact_person_key_002', 'David Wilson'),
  ('email_key_003', 'david@nextgen.com'),
  ('phone_key_004', '555-0105'),
  ('status_key_005', 'inactive'),
  ('revenue_key_006', '1500000'),
  ('industry_key_007', 'Manufacturing'),
  ('location_key_008', 'Detroit, MI')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 10: Insert Values for Lead 6 - Prime Services Group
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 6, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Prime Services Group'),
  ('contact_person_key_002', 'Lisa Martinez'),
  ('email_key_003', 'lisa@primeservices.com'),
  ('phone_key_004', '555-0106'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '6500000'),
  ('industry_key_007', 'Services'),
  ('location_key_008', 'Houston, TX')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 11: Insert Values for Lead 7 - Digital Dynamics
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 7, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Digital Dynamics'),
  ('contact_person_key_002', 'Robert Taylor'),
  ('email_key_003', 'robert@digitaldynamics.com'),
  ('phone_key_004', '555-0107'),
  ('status_key_005', 'pending'),
  ('revenue_key_006', '4100000'),
  ('industry_key_007', 'Marketing'),
  ('location_key_008', 'Miami, FL')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 12: Insert Values for Lead 8 - Enterprise Solutions Inc
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 8, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Enterprise Solutions Inc'),
  ('contact_person_key_002', 'Jennifer Anderson'),
  ('email_key_003', 'jennifer@enterprisesolutions.com'),
  ('phone_key_004', '555-0108'),
  ('status_key_005', 'inactive'),
  ('revenue_key_006', '12000000'),
  ('industry_key_007', 'Enterprise Software'),
  ('location_key_008', 'Seattle, WA')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 13: Insert Values for Lead 9 - CloudTech Systems
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 9, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'CloudTech Systems'),
  ('contact_person_key_002', 'Christopher Lee'),
  ('email_key_003', 'chris@cloudtech.com'),
  ('phone_key_004', '555-0109'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '7800000'),
  ('industry_key_007', 'Cloud Computing'),
  ('location_key_008', 'Austin, TX')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- STEP 14: Insert Values for Lead 10 - Future Ventures LLC
-- ============================================================

INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 10, value, NOW(), NOW(), 'system', 'system' FROM (VALUES
  ('company_name_key_001', 'Future Ventures LLC'),
  ('contact_person_key_002', 'Amanda White'),
  ('email_key_003', 'amanda@futureventures.com'),
  ('phone_key_004', '555-0110'),
  ('status_key_005', 'active'),
  ('revenue_key_006', '9200000'),
  ('industry_key_007', 'Venture Capital'),
  ('location_key_008', 'Boston, MA')
) AS v(key, value)
JOIN template_form_default tfd ON tfd.key = v.key AND tfd.template_id = 1;

-- ============================================================
-- VERIFICATION QUERIES
-- ============================================================

-- Check counts
SELECT
    'Data inserted successfully!' as status,
    (SELECT COUNT(*) FROM menu WHERE id = 4) as menu_created,
    (SELECT COUNT(*) FROM template WHERE id = 1) as template_created,
    (SELECT COUNT(*) FROM template_form_default WHERE template_id = 1) as columns_created,
    (SELECT COUNT(*) FROM lead WHERE id BETWEEN 1 AND 10) as leads_created,
    (SELECT COUNT(*) FROM template_form_value_default WHERE record_id BETWEEN 1 AND 10) as values_created;

-- View column definitions
SELECT id, key, label, type, priority
FROM template_form_default
WHERE template_id = 1
ORDER BY priority;

-- View a sample record (Lead 1)
SELECT tfd.label, tfvd.value
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfvd.record_id = 1
ORDER BY tfd.priority;

-- Count records by status
SELECT tfvd.value as status, COUNT(DISTINCT tfvd.record_id) as count
FROM template_form_value_default tfvd
JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
WHERE tfd.key = 'status_key_005'
GROUP BY tfvd.value;

-- ============================================================
-- SUCCESS!
-- You should see:
--   - menu_created: 1
--   - template_created: 1
--   - columns_created: 8
--   - leads_created: 10
--   - values_created: 80
--
-- Now you can test the API:
--   GET  /api/template-forms-default/template/4
--   GET  /api/dynamic-records/template/4?page=0&size=10
--   POST /api/dynamic-records/query-simple
-- ============================================================
