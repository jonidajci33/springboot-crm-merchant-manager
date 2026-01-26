INSERT INTO menu (id, label, created_at, updated_at, created_by, last_updated_by)
VALUES
    (4, 'Leads', NOW(), NOW(), 'system', 'system'),
    (5, 'Contact', NOW(), NOW(), 'system', 'system'),
    (6, 'Merchant', NOW(), NOW(), 'system', 'system')
ON CONFLICT (id) DO UPDATE
SET
    label = EXCLUDED.label,
    updated_at = EXCLUDED.updated_at,
    last_updated_by = EXCLUDED.last_updated_by;
