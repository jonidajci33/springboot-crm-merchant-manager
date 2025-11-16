-- ============================================
-- Merchant Manager CRM - Database Schema
-- ============================================
-- This script creates all tables for the Merchant Manager CRM system
-- Run this script on a fresh PostgreSQL database
-- ============================================

-- ============================================
-- 1. USER TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS _user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(50),
    company_name VARCHAR(255),
    job_title VARCHAR(255),
    account_status VARCHAR(50),
    role VARCHAR(50) NOT NULL
);

-- ============================================
-- 2. MENU TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS menu (
    id BIGSERIAL PRIMARY KEY,
    label VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 3. TEMPLATE TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES _user(id) ON DELETE CASCADE,
    menu_id BIGINT NOT NULL REFERENCES menu(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 4. TEMPLATE FORM TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template_form (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES template(id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL UNIQUE,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    options JSONB,
    form_props JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 5. TEMPLATE DEFAULT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template_default (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES _user(id) ON DELETE SET NULL,
    menu_id BIGINT REFERENCES menu(id) ON DELETE SET NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 6. TEMPLATE FORM DEFAULT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template_form_default (
    id BIGSERIAL PRIMARY KEY,
    template_default_id BIGINT REFERENCES template_default(id) ON DELETE CASCADE,
    key VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50) NOT NULL,
    options JSONB,
    form_props JSONB,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 7. MERCHANT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS merchant (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 8. CONTACT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS contact (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 9. LEAD TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS lead (
    id BIGSERIAL PRIMARY KEY,
    is_signed BOOLEAN DEFAULT FALSE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 10. CONTACT MERCHANT TABLE (Junction)
-- ============================================
CREATE TABLE IF NOT EXISTS contact_merchant (
    id BIGSERIAL PRIMARY KEY,
    contact_id BIGINT NOT NULL REFERENCES contact(id) ON DELETE CASCADE,
    merchant_id BIGINT NOT NULL REFERENCES merchant(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    UNIQUE(contact_id, merchant_id)
);

-- ============================================
-- 11. MERCHANT TPL TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS merchant_tpl (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchant(id) ON DELETE CASCADE,
    template_id BIGINT NOT NULL REFERENCES template(id) ON DELETE CASCADE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 12. TEMPLATE FORM VALUE TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template_form_value (
    id BIGSERIAL PRIMARY KEY,
    merchant_tpl_id BIGINT REFERENCES merchant_tpl(id) ON DELETE CASCADE,
    template_form_id BIGINT REFERENCES template_form(id) ON DELETE CASCADE,
    value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 13. TEMPLATE FORM VALUE DEFAULT TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS template_form_value_default (
    id BIGSERIAL PRIMARY KEY,
    template_form_default_id BIGINT REFERENCES template_form_default(id) ON DELETE CASCADE,
    value TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

-- ============================================
-- 14. POINTING SYSTEM TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS pointing_system (
    id BIGSERIAL PRIMARY KEY,
    plan VARCHAR(50) NOT NULL,
    pricing_type VARCHAR(100),
    card_fee REAL,
    bps REAL
);

-- ============================================
-- 15. MERCHANT MILES TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS merchant_miles (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchant(id) ON DELETE CASCADE,
    points BIGINT DEFAULT 0,
    pointing_system_id BIGINT REFERENCES pointing_system(id) ON DELETE SET NULL,
    user_id BIGINT REFERENCES _user(id) ON DELETE SET NULL,
    UNIQUE(merchant_id)
);

-- ============================================
-- 16. DEJAVOO CREDENTIALS TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS dejavoo_credentials (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT NOT NULL REFERENCES merchant(id) ON DELETE CASCADE,
    register_id VARCHAR(255) NOT NULL,
    auth_key VARCHAR(500) NOT NULL,
    api_url VARCHAR(500) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    UNIQUE(merchant_id)
);

-- ============================================
-- 17. FILE METADATA TABLE
-- ============================================
CREATE TABLE IF NOT EXISTS file_metadata (
    id BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(500) NOT NULL,
    stored_filename VARCHAR(500) NOT NULL,
    file_path VARCHAR(1000) NOT NULL,
    file_size BIGINT NOT NULL,
    content_type VARCHAR(255),
    cloud_provider VARCHAR(50) NOT NULL,
    bucket_name VARCHAR(255),
    file_url VARCHAR(1000),
    uploaded_by BIGINT REFERENCES _user(id) ON DELETE SET NULL,
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    is_public BOOLEAN DEFAULT FALSE,
    entity_type VARCHAR(100),
    entity_id BIGINT
);

-- ============================================
-- Comments for documentation
-- ============================================

COMMENT ON TABLE _user IS 'Stores user account information including authentication details';
COMMENT ON TABLE menu IS 'Menu configuration for organizing templates';
COMMENT ON TABLE template IS 'Templates for dynamic forms associated with menus';
COMMENT ON TABLE template_form IS 'Form fields within templates with JSONB options and properties';
COMMENT ON TABLE template_default IS 'Default template configurations';
COMMENT ON TABLE template_form_default IS 'Default form field configurations';
COMMENT ON TABLE merchant IS 'Merchant records in the CRM system';
COMMENT ON TABLE contact IS 'Contact records in the CRM system';
COMMENT ON TABLE lead IS 'Lead tracking with signing and active status';
COMMENT ON TABLE contact_merchant IS 'Junction table linking contacts to merchants';
COMMENT ON TABLE merchant_tpl IS 'Links merchants to templates with form values';
COMMENT ON TABLE template_form_value IS 'Stores values for template form fields per merchant';
COMMENT ON TABLE template_form_value_default IS 'Default values for template form fields';
COMMENT ON TABLE pointing_system IS 'Merchant miles pointing system configurations with BPS rates';
COMMENT ON TABLE merchant_miles IS 'Tracks merchant miles points and enrollments';
COMMENT ON TABLE dejavoo_credentials IS 'Stores Dejavoo payment processor API credentials per merchant';
COMMENT ON TABLE file_metadata IS 'Metadata for files stored in cloud storage (Supabase)';
