-- ============================================
-- Merchant Manager CRM - Drop All Tables
-- ============================================
-- WARNING: This script will DELETE ALL DATA
-- Use with extreme caution!
-- Only run this script if you want to completely reset the database
-- ============================================

-- ============================================
-- DISABLE FOREIGN KEY CHECKS (PostgreSQL)
-- ============================================
-- In PostgreSQL, we drop tables in reverse dependency order
-- Or use CASCADE to automatically drop dependent objects

-- ============================================
-- DROP TABLES IN REVERSE ORDER
-- ============================================
-- Drop tables that have foreign keys first, then tables they reference

-- Drop file metadata (references user)
DROP TABLE IF EXISTS file_metadata CASCADE;

-- Drop dejavoo credentials (references merchant)
DROP TABLE IF EXISTS dejavoo_credentials CASCADE;

-- Drop merchant miles (references merchant, pointing_system, user)
DROP TABLE IF EXISTS merchant_miles CASCADE;

-- Drop pointing system
DROP TABLE IF EXISTS pointing_system CASCADE;

-- Drop template form value default (references template_form_default)
DROP TABLE IF EXISTS template_form_value_default CASCADE;

-- Drop template form value (references merchant_tpl, template_form)
DROP TABLE IF EXISTS template_form_value CASCADE;

-- Drop merchant tpl (references merchant, template)
DROP TABLE IF EXISTS merchant_tpl CASCADE;

-- Drop contact merchant (references contact, merchant)
DROP TABLE IF EXISTS contact_merchant CASCADE;

-- Drop lead
DROP TABLE IF EXISTS lead CASCADE;

-- Drop contact
DROP TABLE IF EXISTS contact CASCADE;

-- Drop merchant
DROP TABLE IF EXISTS merchant CASCADE;

-- Drop template form default (references template_default)
DROP TABLE IF EXISTS template_form_default CASCADE;

-- Drop template default (references user, menu)
DROP TABLE IF EXISTS template_default CASCADE;

-- Drop template form (references template)
DROP TABLE IF EXISTS template_form CASCADE;

-- Drop template (references user, menu)
DROP TABLE IF EXISTS template CASCADE;

-- Drop menu
DROP TABLE IF EXISTS menu CASCADE;

-- Drop user (last because many tables reference it)
DROP TABLE IF EXISTS _user CASCADE;

-- ============================================
-- DROP ALL INDEXES (if they still exist)
-- ============================================
-- Note: Indexes are automatically dropped when tables are dropped
-- This section is just for reference

-- ============================================
-- VERIFICATION
-- ============================================
-- After running this script, verify all tables are dropped:
-- SELECT tablename FROM pg_tables WHERE schemaname = 'public';

-- ============================================
-- RESET SEQUENCES (Optional)
-- ============================================
-- If you want to reset all sequences to start from 1
-- Run this after dropping tables and before recreating them:

-- DO $$
-- DECLARE
--     r RECORD;
-- BEGIN
--     FOR r IN (SELECT sequence_name FROM information_schema.sequences WHERE sequence_schema = 'public')
--     LOOP
--         EXECUTE 'ALTER SEQUENCE ' || quote_ident(r.sequence_name) || ' RESTART WITH 1;';
--     END LOOP;
-- END $$;

-- ============================================
-- NOTES
-- ============================================
-- 1. CASCADE will automatically drop dependent objects (foreign keys, indexes, etc.)
-- 2. This script is irreversible - all data will be permanently lost
-- 3. Always backup your database before running this script
-- 4. In production, consider using database migrations instead of drop/create
-- 5. After dropping tables, you can run 01_create_tables.sql to recreate them
