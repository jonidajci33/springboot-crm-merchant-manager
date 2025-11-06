-- ============================================================
-- Create Missing Data
-- Run this if you're missing User 3, Menu 4, or Template 1
-- ============================================================

-- STEP 1: Check what exists
SELECT 'Checking existing data...' as status;

SELECT
    CASE
        WHEN EXISTS (SELECT 1 FROM users WHERE id = 3)
        THEN '✅ User ID 3 already exists'
        ELSE '❌ User ID 3 does not exist'
    END as user_check,
    CASE
        WHEN EXISTS (SELECT 1 FROM menu WHERE id = 4)
        THEN '✅ Menu ID 4 already exists'
        ELSE '❌ Menu ID 4 does not exist'
    END as menu_check,
    CASE
        WHEN EXISTS (SELECT 1 FROM template WHERE id = 1)
        THEN '✅ Template ID 1 already exists'
        ELSE '❌ Template ID 1 does not exist'
    END as template_check;

-- STEP 2: Create Menu ID 4 if it doesn't exist
-- Uncomment if you need to create the menu
/*
INSERT INTO menu (id, name, created_at, updated_at, created_by, last_updated_by)
VALUES (4, 'Leads', NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

SELECT setval('menu_id_seq', (SELECT MAX(id) FROM menu));
*/

-- STEP 3: Check if User ID 3 exists
-- You cannot easily create a user without password hashing
-- If User 3 doesn't exist, you need to:
-- 1. Use your existing user registration endpoint
-- 2. Or manually insert with a hashed password
-- Example (requires bcrypt password hash):
/*
INSERT INTO users (id, username, email, password, role, created_at, updated_at)
VALUES (
    3,
    'testuser',
    'testuser@example.com',
    '$2a$10$example_hashed_password', -- Replace with actual bcrypt hash
    'ROLE_USER',
    NOW(),
    NOW()
)
ON CONFLICT (id) DO NOTHING;

SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
*/

-- STEP 4: Create or fix Template ID 1
-- This links User 3 and Menu 4

-- Delete template if it exists with wrong IDs
DELETE FROM template
WHERE id = 1 AND (user_id != 3 OR menu_id != 4);

-- Create template if it doesn't exist
INSERT INTO template (id, user_id, menu_id, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 3, 4, NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Reset sequence
SELECT setval('template_id_seq', GREATEST((SELECT MAX(id) FROM template), 1));

-- STEP 5: Verify everything is set up correctly
SELECT
    t.id as template_id,
    t.user_id,
    u.username,
    u.email,
    t.menu_id,
    m.name as menu_name
FROM template t
LEFT JOIN users u ON t.user_id = u.id
LEFT JOIN menu m ON t.menu_id = m.id
WHERE t.id = 1;

-- If the above returns a row with all columns filled, you're ready to seed!
-- Run: sample-data.sql
