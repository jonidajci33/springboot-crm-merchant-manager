-- Sample Data for Dynamic Records API Testing
-- This script inserts sample data for testing the Dynamic Records API

-- ============================================================
-- CONFIGURED FOR:
-- - User ID: 3
-- - Menu ID: 4
-- - Template ID: 1
-- ============================================================
-- IMPORTANT: Make sure these exist in your database before running:
-- 1. User with ID 3
-- 2. Menu with ID 4
-- 3. Template with ID 1 (linking User 3 and Menu 4)

-- If template doesn't exist, create it:
-- INSERT INTO template (id, user_id, menu_id, created_at, updated_at, created_by, last_updated_by) VALUES
-- (1, 3, 4, NOW(), NOW(), 'system', 'system') ON CONFLICT (id) DO NOTHING;

-- ============================================================
-- STEP 1: Insert Column Definitions (TemplateFormDefault)
-- ============================================================
-- These define the structure of the grid (table headers)

INSERT INTO template_form_default (template_id, key, label, type, priority, options, style, created_at, updated_at, created_by, last_updated_by) VALUES
-- Column 1: Company Name
(1, 'company_name_key_001', 'Company Name', 'TEXT', 1, NULL,
 '{"width": "200px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 2: Contact Person
(1, 'contact_person_key_002', 'Contact Person', 'TEXT', 2, NULL,
 '{"width": "180px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 3: Email
(1, 'email_key_003', 'Email Address', 'TEXT', 3, NULL,
 '{"width": "200px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 4: Phone
(1, 'phone_key_004', 'Phone Number', 'PHONE_NUMBER', 4, NULL,
 '{"width": "150px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 5: Status
(1, 'status_key_005', 'Status', 'DROPDOWN', 5,
 '{"active": "Active", "inactive": "Inactive", "pending": "Pending"}'::jsonb,
 '{"width": "120px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 6: Revenue
(1, 'revenue_key_006', 'Annual Revenue', 'NUMBER', 6, NULL,
 '{"width": "150px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 7: Industry
(1, 'industry_key_007', 'Industry', 'DROPDOWN', 7,
 '{"technology": "Technology", "software": "Software", "consulting": "Consulting", "design": "Design", "manufacturing": "Manufacturing", "services": "Services", "marketing": "Marketing", "enterprise_software": "Enterprise Software", "cloud_computing": "Cloud Computing", "venture_capital": "Venture Capital"}'::jsonb,
 '{"width": "150px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin'),

-- Column 8: Location
(1, 'location_key_008', 'Location', 'TEXT', 8, NULL,
 '{"width": "180px"}'::jsonb,
 NOW(), NOW(), 'admin', 'admin');

-- ============================================================
-- STEP 2: Insert Actual Records (Lead entities)
-- ============================================================
-- These are the actual lead/contact/merchant records
-- Adjust the table name based on your entity (lead, contact, or merchant)

-- Insert sample leads (if using Lead entity)
INSERT INTO lead (is_signed, is_active, created_at, updated_at, created_by, last_updated_by) VALUES
(true, true, NOW(), NOW(), 'admin', 'admin'),   -- Record ID will be auto-generated (e.g., 1)
(false, true, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 2
(false, true, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 3
(true, true, NOW(), NOW(), 'admin', 'admin'),   -- Record ID: 4
(false, true, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 5
(true, true, NOW(), NOW(), 'admin', 'admin'),   -- Record ID: 6
(false, true, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 7
(true, false, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 8
(false, true, NOW(), NOW(), 'admin', 'admin'),  -- Record ID: 9
(true, true, NOW(), NOW(), 'admin', 'admin');   -- Record ID: 10

-- ============================================================
-- STEP 3: Insert Values for Records (TemplateFormValueDefault)
-- ============================================================
-- These are the actual field values for each record (EAV pattern)

-- Record 1: Acme Corporation
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 1, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 2: TechStart Inc
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 2, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 3: Global Solutions Ltd
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 3, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 4: Innovative Designs Co
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 4, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 5: NextGen Industries
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 5, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 6: Prime Services Group
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 6, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 7: Digital Dynamics
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 7, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 8: Enterprise Solutions Inc
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 8, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 9: CloudTech Systems
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 9, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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

-- Record 10: Future Ventures LLC
INSERT INTO template_form_value_default (template_form_default_id, record_id, value, created_at, updated_at, created_by, last_updated_by)
SELECT id, 10, value, NOW(), NOW(), 'admin', 'admin' FROM (VALUES
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
-- Verification Queries
-- ============================================================
-- Run these to verify the data was inserted correctly:

-- Check column definitions
-- SELECT * FROM template_form_default WHERE template_id = 1 ORDER BY priority;

-- Check records
-- SELECT * FROM lead ORDER BY id LIMIT 10;

-- Check values for a specific record
-- SELECT tfd.label, tfvd.value
-- FROM template_form_value_default tfvd
-- JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
-- WHERE tfvd.record_id = 1
-- ORDER BY tfd.priority;

-- Count records per status
-- SELECT tfvd.value as status, COUNT(DISTINCT tfvd.record_id) as count
-- FROM template_form_value_default tfvd
-- JOIN template_form_default tfd ON tfvd.template_form_default_id = tfd.id
-- WHERE tfd.key = 'status_key_005'
-- GROUP BY tfvd.value;
