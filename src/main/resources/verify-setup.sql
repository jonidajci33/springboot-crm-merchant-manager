-- ============================================================
-- Verify Setup for Sample Data
-- This checks if you have the required data before seeding
-- ============================================================

-- 1. Check if User ID 3 exists
SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM users WHERE id = 3)
        THEN '✅ User ID 3 exists'
        ELSE '❌ User ID 3 NOT FOUND - Please create it first'
    END as user_check;

-- 2. Check if Menu ID 4 exists
SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM menu WHERE id = 4)
        THEN '✅ Menu ID 4 exists'
        ELSE '❌ Menu ID 4 NOT FOUND - Please create it first'
    END as menu_check;

-- 3. Check if Template ID 1 exists
SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM template WHERE id = 1)
        THEN '✅ Template ID 1 exists'
        ELSE '❌ Template ID 1 NOT FOUND - Please create it first'
    END as template_check;

-- 4. If template exists, verify it links User 3 and Menu 4
SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM template WHERE id = 1 AND user_id = 3 AND menu_id = 4)
        THEN '✅ Template ID 1 correctly links User 3 and Menu 4'
        WHEN EXISTS (SELECT 1 FROM template WHERE id = 1)
        THEN '⚠️  Template ID 1 exists but has wrong user_id or menu_id'
        ELSE '❌ Template ID 1 does not exist'
    END as template_linkage_check;

-- 5. Show current template details
SELECT
    t.id as template_id,
    t.user_id,
    u.username,
    t.menu_id,
    m.name as menu_name
FROM template t
LEFT JOIN users u ON t.user_id = u.id
LEFT JOIN menu m ON t.menu_id = m.id
WHERE t.id = 1;

-- 6. If template doesn't exist or is wrong, here's the fix:
/*
-- Delete wrong template if exists
DELETE FROM template WHERE id = 1;

-- Create correct template
INSERT INTO template (id, user_id, menu_id, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 3, 4, NOW(), NOW(), 'system', 'system');

-- Reset sequence (PostgreSQL)
SELECT setval('template_id_seq', (SELECT MAX(id) FROM template));
*/

-- 7. Summary
SELECT
    (SELECT COUNT(*) FROM users WHERE id = 3) as user_3_exists,
    (SELECT COUNT(*) FROM menu WHERE id = 4) as menu_4_exists,
    (SELECT COUNT(*) FROM template WHERE id = 1 AND user_id = 3 AND menu_id = 4) as correct_template_exists;
