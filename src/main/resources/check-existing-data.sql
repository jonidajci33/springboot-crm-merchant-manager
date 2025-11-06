-- ============================================================
-- Check Existing Data Script
-- Run this first to see what data you already have
-- ============================================================

-- 1. Check Users
SELECT id, username, email, role FROM users ORDER BY id;

-- 2. Check Menus
SELECT id, name FROM menu ORDER BY id;

-- 3. Check Templates (Links users to menus)
SELECT t.id as template_id, t.user_id, u.username, t.menu_id, m.name as menu_name
FROM template t
LEFT JOIN users u ON t.user_id = u.id
LEFT JOIN menu m ON t.menu_id = m.id
ORDER BY t.id;

-- 4. Check existing TemplateFormDefault (Column Definitions)
SELECT tfd.id, tfd.template_id, tfd.key, tfd.label, tfd.type, tfd.priority
FROM template_form_default tfd
ORDER BY tfd.template_id, tfd.priority;

-- 5. Check existing Leads
SELECT id, is_signed, is_active FROM lead ORDER BY id LIMIT 20;

-- 6. Check existing TemplateFormValueDefault (Values)
SELECT tfvd.id, tfvd.template_form_default_id, tfvd.record_id, tfvd.value
FROM template_form_value_default tfvd
ORDER BY tfvd.record_id, tfvd.template_form_default_id
LIMIT 50;

-- 7. Count records by table
SELECT
    (SELECT COUNT(*) FROM users) as user_count,
    (SELECT COUNT(*) FROM menu) as menu_count,
    (SELECT COUNT(*) FROM template) as template_count,
    (SELECT COUNT(*) FROM template_form_default) as column_count,
    (SELECT COUNT(*) FROM lead) as lead_count,
    (SELECT COUNT(*) FROM template_form_value_default) as value_count;
