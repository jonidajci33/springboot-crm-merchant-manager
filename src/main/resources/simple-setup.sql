-- ============================================================
-- Simple Setup Script
-- Creates Menu 4 and Template 1 (linking User 3 and Menu 4)
-- ============================================================

-- Create Menu ID 4 if it doesn't exist
INSERT INTO menu (id, name, created_at, updated_at, created_by, last_updated_by)
VALUES (4, 'Leads', NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Update sequence
SELECT setval('menu_id_seq', GREATEST((SELECT MAX(id) FROM menu), 1));

-- Create Template ID 1 if it doesn't exist
INSERT INTO template (id, user_id, menu_id, created_at, updated_at, created_by, last_updated_by)
VALUES (1, 3, 4, NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO NOTHING;

-- Update sequence
SELECT setval('template_id_seq', GREATEST((SELECT MAX(id) FROM template), 1));

-- Verify
SELECT
    'Setup Complete!' as status,
    (SELECT COUNT(*) FROM menu WHERE id = 4) as menu_4_exists,
    (SELECT COUNT(*) FROM template WHERE id = 1) as template_1_exists;

-- Show the template details
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
