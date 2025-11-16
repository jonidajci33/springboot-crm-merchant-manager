-- ============================================
-- Merchant Manager CRM - Schema Verification
-- ============================================
-- This script verifies the database schema is set up correctly
-- Run this after creating tables to ensure everything is in place
-- ============================================

-- ============================================
-- 1. CHECK ALL TABLES EXIST
-- ============================================
SELECT
    'Checking Tables...' AS check_type,
    COUNT(*) AS table_count,
    CASE
        WHEN COUNT(*) = 17 THEN 'PASS ✓'
        ELSE 'FAIL ✗ - Expected 17 tables'
    END AS status
FROM information_schema.tables
WHERE table_schema = 'public'
    AND table_type = 'BASE TABLE';

-- List all tables
SELECT
    'Table List' AS info,
    tablename AS table_name,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY tablename;

-- ============================================
-- 2. CHECK FOREIGN KEY CONSTRAINTS
-- ============================================
SELECT
    'Checking Foreign Keys...' AS check_type,
    COUNT(*) AS fk_count,
    CASE
        WHEN COUNT(*) >= 15 THEN 'PASS ✓'
        ELSE 'FAIL ✗ - Some foreign keys missing'
    END AS status
FROM information_schema.table_constraints
WHERE constraint_type = 'FOREIGN KEY'
    AND table_schema = 'public';

-- List all foreign keys
SELECT
    tc.table_name,
    kcu.column_name,
    ccu.table_name AS foreign_table_name,
    ccu.column_name AS foreign_column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
    AND tc.table_schema = kcu.table_schema
JOIN information_schema.constraint_column_usage AS ccu
    ON ccu.constraint_name = tc.constraint_name
    AND ccu.table_schema = tc.table_schema
WHERE tc.constraint_type = 'FOREIGN KEY'
    AND tc.table_schema = 'public'
ORDER BY tc.table_name, kcu.column_name;

-- ============================================
-- 3. CHECK INDEXES
-- ============================================
SELECT
    'Checking Indexes...' AS check_type,
    COUNT(*) AS index_count,
    CASE
        WHEN COUNT(*) >= 30 THEN 'PASS ✓'
        ELSE 'WARNING ⚠ - Some indexes may be missing'
    END AS status
FROM pg_indexes
WHERE schemaname = 'public';

-- List all indexes
SELECT
    schemaname,
    tablename,
    indexname,
    indexdef
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY tablename, indexname;

-- ============================================
-- 4. CHECK UNIQUE CONSTRAINTS
-- ============================================
SELECT
    'Checking Unique Constraints...' AS check_type,
    COUNT(*) AS unique_count,
    'INFO' AS status
FROM information_schema.table_constraints
WHERE constraint_type = 'UNIQUE'
    AND table_schema = 'public';

-- List unique constraints
SELECT
    tc.table_name,
    tc.constraint_name,
    kcu.column_name
FROM information_schema.table_constraints AS tc
JOIN information_schema.key_column_usage AS kcu
    ON tc.constraint_name = kcu.constraint_name
WHERE tc.constraint_type = 'UNIQUE'
    AND tc.table_schema = 'public'
ORDER BY tc.table_name, kcu.column_name;

-- ============================================
-- 5. CHECK JSONB COLUMNS
-- ============================================
SELECT
    'Checking JSONB Columns...' AS check_type,
    COUNT(*) AS jsonb_count,
    CASE
        WHEN COUNT(*) >= 4 THEN 'PASS ✓'
        ELSE 'FAIL ✗ - Expected at least 4 JSONB columns'
    END AS status
FROM information_schema.columns
WHERE table_schema = 'public'
    AND data_type = 'jsonb';

-- List JSONB columns
SELECT
    table_name,
    column_name,
    data_type
FROM information_schema.columns
WHERE table_schema = 'public'
    AND data_type = 'jsonb'
ORDER BY table_name, column_name;

-- ============================================
-- 6. CHECK SEED DATA
-- ============================================
-- Verify seed data was inserted correctly

SELECT 'User Accounts' AS data_type, COUNT(*) AS count FROM _user;
SELECT 'Menus' AS data_type, COUNT(*) AS count FROM menu;
SELECT 'Pointing Systems' AS data_type, COUNT(*) AS count FROM pointing_system;
SELECT 'Merchants' AS data_type, COUNT(*) AS count FROM merchant;
SELECT 'Contacts' AS data_type, COUNT(*) AS count FROM contact;
SELECT 'Leads' AS data_type, COUNT(*) AS count FROM lead;

-- ============================================
-- 7. CHECK COLUMN TYPES
-- ============================================
-- Verify important columns have correct data types

SELECT
    'Column Type Verification' AS check_type,
    table_name,
    column_name,
    data_type,
    CASE
        WHEN is_nullable = 'NO' THEN 'NOT NULL'
        ELSE 'NULLABLE'
    END AS nullable
FROM information_schema.columns
WHERE table_schema = 'public'
    AND table_name IN ('_user', 'merchant', 'merchant_miles', 'file_metadata')
    AND column_name IN ('id', 'email', 'username', 'points', 'file_size')
ORDER BY table_name, column_name;

-- ============================================
-- 8. CHECK SEQUENCES
-- ============================================
-- Verify auto-increment sequences exist

SELECT
    'Checking Sequences...' AS check_type,
    COUNT(*) AS sequence_count,
    CASE
        WHEN COUNT(*) >= 17 THEN 'PASS ✓'
        ELSE 'FAIL ✗ - Missing sequences'
    END AS status
FROM information_schema.sequences
WHERE sequence_schema = 'public';

-- List sequences
SELECT
    sequence_name,
    data_type,
    start_value,
    minimum_value,
    maximum_value,
    increment
FROM information_schema.sequences
WHERE sequence_schema = 'public'
ORDER BY sequence_name;

-- ============================================
-- 9. CHECK TABLE STATISTICS
-- ============================================
-- View table sizes and row counts

SELECT
    schemaname,
    tablename,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename)) AS total_size,
    pg_size_pretty(pg_relation_size(schemaname||'.'||tablename)) AS table_size,
    pg_size_pretty(pg_total_relation_size(schemaname||'.'||tablename) - pg_relation_size(schemaname||'.'||tablename)) AS index_size
FROM pg_tables
WHERE schemaname = 'public'
ORDER BY pg_total_relation_size(schemaname||'.'||tablename) DESC;

-- ============================================
-- 10. SUMMARY REPORT
-- ============================================
SELECT
    '=== SCHEMA VERIFICATION SUMMARY ===' AS report,
    '' AS details
UNION ALL
SELECT
    'Tables Created:',
    COUNT(*)::TEXT
FROM information_schema.tables
WHERE table_schema = 'public' AND table_type = 'BASE TABLE'
UNION ALL
SELECT
    'Foreign Keys:',
    COUNT(*)::TEXT
FROM information_schema.table_constraints
WHERE constraint_type = 'FOREIGN KEY' AND table_schema = 'public'
UNION ALL
SELECT
    'Indexes:',
    COUNT(*)::TEXT
FROM pg_indexes
WHERE schemaname = 'public'
UNION ALL
SELECT
    'Unique Constraints:',
    COUNT(*)::TEXT
FROM information_schema.table_constraints
WHERE constraint_type = 'UNIQUE' AND table_schema = 'public'
UNION ALL
SELECT
    'JSONB Columns:',
    COUNT(*)::TEXT
FROM information_schema.columns
WHERE table_schema = 'public' AND data_type = 'jsonb'
UNION ALL
SELECT
    'Sequences:',
    COUNT(*)::TEXT
FROM information_schema.sequences
WHERE sequence_schema = 'public'
UNION ALL
SELECT
    'Total Database Size:',
    pg_size_pretty(pg_database_size(current_database()));

-- ============================================
-- 11. POTENTIAL ISSUES CHECK
-- ============================================
-- Check for potential issues

-- Tables without primary keys (should be none)
SELECT
    'Tables Without Primary Keys' AS issue_type,
    table_name
FROM information_schema.tables t
WHERE table_schema = 'public'
    AND table_type = 'BASE TABLE'
    AND NOT EXISTS (
        SELECT 1
        FROM information_schema.table_constraints tc
        WHERE tc.table_name = t.table_name
            AND tc.table_schema = t.table_schema
            AND tc.constraint_type = 'PRIMARY KEY'
    );

-- Columns that should be NOT NULL but aren't
SELECT
    'Potentially Missing NOT NULL Constraints' AS issue_type,
    table_name,
    column_name
FROM information_schema.columns
WHERE table_schema = 'public'
    AND is_nullable = 'YES'
    AND column_name IN ('id', 'created_at', 'updated_at', 'created_by')
ORDER BY table_name, column_name;

-- ============================================
-- VERIFICATION COMPLETE
-- ============================================
SELECT
    '✓ Schema verification complete!' AS status,
    'Review results above for any issues.' AS message;
