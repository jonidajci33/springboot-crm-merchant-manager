- ============================================
-- Merchant Manager CRM - Indexes and Constraints
-- ============================================
-- This script creates indexes and additional constraints
-- for optimal database performance
-- ============================================

-- ============================================
-- INDEXES FOR USER TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_user_username ON _user(username);
CREATE INDEX IF NOT EXISTS idx_user_email ON _user(email);
CREATE INDEX IF NOT EXISTS idx_user_role ON _user(role);
CREATE INDEX IF NOT EXISTS idx_user_account_status ON _user(account_status);

-- ============================================
-- INDEXES FOR TEMPLATE TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_user_id ON template(user_id);
CREATE INDEX IF NOT EXISTS idx_template_menu_id ON template(menu_id);
CREATE INDEX IF NOT EXISTS idx_template_created_at ON template(created_at DESC);

-- ============================================
-- INDEXES FOR TEMPLATE FORM TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_form_template_id ON template_form(template_id);
CREATE INDEX IF NOT EXISTS idx_template_form_key ON template_form(key);
CREATE INDEX IF NOT EXISTS idx_template_form_type ON template_form(type);

-- For JSONB columns - GIN indexes for faster JSON queries
CREATE INDEX IF NOT EXISTS idx_template_form_options ON template_form USING GIN (options);
CREATE INDEX IF NOT EXISTS idx_template_form_props ON template_form USING GIN (form_props);

-- ============================================
-- INDEXES FOR TEMPLATE DEFAULT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_default_user_id ON template_default(user_id);
CREATE INDEX IF NOT EXISTS idx_template_default_menu_id ON template_default(menu_id);

-- ============================================
-- INDEXES FOR TEMPLATE FORM DEFAULT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_form_default_template_id ON template_form_default(template_default_id);
CREATE INDEX IF NOT EXISTS idx_template_form_default_key ON template_form_default(key);

-- GIN indexes for JSONB columns
CREATE INDEX IF NOT EXISTS idx_template_form_default_options ON template_form_default USING GIN (options);
CREATE INDEX IF NOT EXISTS idx_template_form_default_props ON template_form_default USING GIN (form_props);

-- ============================================
-- INDEXES FOR MERCHANT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_merchant_created_at ON merchant(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_merchant_created_by ON merchant(created_by);

-- ============================================
-- INDEXES FOR CONTACT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_contact_created_at ON contact(created_at DESC);
CREATE INDEX IF NOT EXISTS idx_contact_created_by ON contact(created_by);

-- ============================================
-- INDEXES FOR LEAD TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_lead_is_signed ON lead(is_signed);
CREATE INDEX IF NOT EXISTS idx_lead_is_active ON lead(is_active);
CREATE INDEX IF NOT EXISTS idx_lead_created_at ON lead(created_at DESC);

-- ============================================
-- INDEXES FOR CONTACT MERCHANT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_contact_merchant_contact_id ON contact_merchant(contact_id);
CREATE INDEX IF NOT EXISTS idx_contact_merchant_merchant_id ON contact_merchant(merchant_id);
CREATE INDEX IF NOT EXISTS idx_contact_merchant_created_at ON contact_merchant(created_at DESC);

-- ============================================
-- INDEXES FOR MERCHANT TPL TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_merchant_tpl_merchant_id ON merchant_tpl(merchant_id);
CREATE INDEX IF NOT EXISTS idx_merchant_tpl_template_id ON merchant_tpl(template_id);
CREATE INDEX IF NOT EXISTS idx_merchant_tpl_created_at ON merchant_tpl(created_at DESC);

-- ============================================
-- INDEXES FOR TEMPLATE FORM VALUE TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_form_value_merchant_tpl_id ON template_form_value(merchant_tpl_id);
CREATE INDEX IF NOT EXISTS idx_template_form_value_template_form_id ON template_form_value(template_form_id);

-- ============================================
-- INDEXES FOR TEMPLATE FORM VALUE DEFAULT TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_template_form_value_default_form_id ON template_form_value_default(template_form_default_id);

-- ============================================
-- INDEXES FOR POINTING SYSTEM TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_pointing_system_plan ON pointing_system(plan);

-- ============================================
-- INDEXES FOR MERCHANT MILES TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_merchant_miles_merchant_id ON merchant_miles(merchant_id);
CREATE INDEX IF NOT EXISTS idx_merchant_miles_user_id ON merchant_miles(user_id);
CREATE INDEX IF NOT EXISTS idx_merchant_miles_pointing_system_id ON merchant_miles(pointing_system_id);
CREATE INDEX IF NOT EXISTS idx_merchant_miles_points ON merchant_miles(points DESC);

-- ============================================
-- INDEXES FOR DEJAVOO CREDENTIALS TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_dejavoo_credentials_merchant_id ON dejavoo_credentials(merchant_id);
CREATE INDEX IF NOT EXISTS idx_dejavoo_credentials_is_active ON dejavoo_credentials(is_active);

-- ============================================
-- INDEXES FOR FILE METADATA TABLE
-- ============================================
CREATE INDEX IF NOT EXISTS idx_file_metadata_uploaded_by ON file_metadata(uploaded_by);
CREATE INDEX IF NOT EXISTS idx_file_metadata_uploaded_at ON file_metadata(uploaded_at DESC);
CREATE INDEX IF NOT EXISTS idx_file_metadata_entity_type_id ON file_metadata(entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_file_metadata_stored_filename ON file_metadata(stored_filename);
CREATE INDEX IF NOT EXISTS idx_file_metadata_cloud_provider ON file_metadata(cloud_provider);
CREATE INDEX IF NOT EXISTS idx_file_metadata_is_public ON file_metadata(is_public);

-- ============================================
-- COMPOSITE INDEXES FOR COMMON QUERIES
-- ============================================

-- Template form lookup by template and key
CREATE INDEX IF NOT EXISTS idx_template_form_template_key ON template_form(template_id, key);

-- Contact merchant by merchant and contact
CREATE INDEX IF NOT EXISTS idx_contact_merchant_composite ON contact_merchant(merchant_id, contact_id);

-- Merchant miles by merchant and pointing system
CREATE INDEX IF NOT EXISTS idx_merchant_miles_merchant_pointing ON merchant_miles(merchant_id, pointing_system_id);

-- File metadata by entity and upload date
CREATE INDEX IF NOT EXISTS idx_file_metadata_entity_date ON file_metadata(entity_type, entity_id, uploaded_at DESC);

-- Active Dejavoo credentials by merchant
CREATE INDEX IF NOT EXISTS idx_dejavoo_active_merchant ON dejavoo_credentials(merchant_id, is_active) WHERE is_active = TRUE;

-- ============================================
-- FULL TEXT SEARCH INDEXES (Optional)
-- ============================================
-- Uncomment if you need full-text search capabilities

-- CREATE INDEX IF NOT EXISTS idx_user_fulltext ON _user USING GIN (to_tsvector('english',
--     COALESCE(name, '') || ' ' ||
--     COALESCE(last_name, '') || ' ' ||
--     COALESCE(email, '') || ' ' ||
--     COALESCE(company_name, '')
-- ));

-- CREATE INDEX IF NOT EXISTS idx_menu_fulltext ON menu USING GIN (to_tsvector('english', COALESCE(label, '')));

-- ============================================
-- PERFORMANCE NOTES
-- ============================================
-- 1. GIN indexes on JSONB columns support queries like @>, ?, ?&, and ?| operators
-- 2. Descending indexes on created_at optimize "latest records" queries
-- 3. Partial indexes (WHERE clauses) reduce index size for filtered queries
-- 4. Composite indexes support multi-column WHERE clauses and JOINs
-- 5. Consider adding more specific indexes based on actual query patterns

-- ============================================
-- ANALYZE TABLES FOR QUERY OPTIMIZATION
-- ============================================
ANALYZE _user;
ANALYZE menu;
ANALYZE template;
ANALYZE template_form;
ANALYZE template_default;
ANALYZE template_form_default;
ANALYZE merchant;
ANALYZE contact;
ANALYZE lead;
ANALYZE contact_merchant;
ANALYZE merchant_tpl;
ANALYZE template_form_value;
ANALYZE template_form_value_default;
ANALYZE pointing_system;
ANALYZE merchant_miles;
ANALYZE dejavoo_credentials;
ANALYZE file_metadata;
