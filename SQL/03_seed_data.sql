-- ============================================
-- Merchant Manager CRM - Seed Data
-- ============================================
-- This script inserts initial data for development and testing
-- Modify or remove as needed for production
-- ============================================

-- ============================================
-- 1. SEED USERS
-- ============================================
-- Password is 'password123' - encoded with BCrypt
-- You should change these passwords in production!

INSERT INTO _user (username, email, name, last_name, password, phone, company_name, job_title, account_status, role)
VALUES
    ('admin', 'admin@merchantmanager.com', 'Admin', 'User', '$2a$10$N.zmdr9k7uOCQQVbfm7E9.7WNxo9YlFPl0y.oE0WjZ5PqKGsZdgqi', '555-0001', 'Merchant Manager Inc', 'System Administrator', 'ACTIVE', 'ADMIN'),
    ('manager', 'manager@merchantmanager.com', 'Manager', 'User', '$2a$10$N.zmdr9k7uOCQQVbfm7E9.7WNxo9YlFPl0y.oE0WjZ5PqKGsZdgqi', '555-0002', 'Merchant Manager Inc', 'Sales Manager', 'ACTIVE', 'MANAGER'),
    ('user', 'user@merchantmanager.com', 'Regular', 'User', '$2a$10$N.zmdr9k7uOCQQVbfm7E9.7WNxo9YlFPl0y.oE0WjZ5PqKGsZdgqi', '555-0003', 'Merchant Manager Inc', 'Sales Representative', 'ACTIVE', 'USER')
ON CONFLICT (username) DO NOTHING;

-- ============================================
-- 2. SEED MENUS
-- ============================================
INSERT INTO menu (label, created_at, updated_at, created_by, last_updated_by)
VALUES
    ('Merchant Information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Business Details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Banking Information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Processing Details', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system'),
    ('Contact Information', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'system', 'system')
ON CONFLICT DO NOTHING;

-- ============================================
-- 3. SEED POINTING SYSTEMS
-- ============================================
INSERT INTO pointing_system (plan, pricing_type, card_fee, bps)
VALUES
    ('BASIC', 'Interchange Plus', 0.10, 25.0),
    ('STANDARD', 'Interchange Plus', 0.08, 20.0),
    ('PREMIUM', 'Interchange Plus', 0.05, 15.0),
    ('ENTERPRISE', 'Flat Rate', 0.03, 10.0)
ON CONFLICT DO NOTHING;

-- ============================================
-- 4. SEED SAMPLE MERCHANTS
-- ============================================
INSERT INTO merchant (created_at, updated_at, created_by, last_updated_by)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin')
RETURNING id;

-- ============================================
-- 5. SEED SAMPLE CONTACTS
-- ============================================
INSERT INTO contact (created_at, updated_at, created_by, last_updated_by)
VALUES
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin')
RETURNING id;

-- ============================================
-- 6. SEED SAMPLE LEADS
-- ============================================
INSERT INTO lead (is_signed, is_active, created_at, updated_at, created_by, last_updated_by)
VALUES
    (FALSE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (TRUE, TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin'),
    (FALSE, FALSE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'admin', 'admin')
ON CONFLICT DO NOTHING;

-- ============================================
-- NOTES
-- ============================================
-- 1. Default password for all seed users is 'password123'
-- 2. BCrypt hash: $2a$10$N.zmdr9k7uOCQQVbfm7E9.7WNxo9YlFPl0y.oE0WjZ5PqKGsZdgqi
-- 3. Change all passwords immediately in production
-- 4. Adjust seed data based on your specific needs
-- 5. Role values should match your Role enum: ADMIN, MANAGER, USER
-- 6. AccountStatus values should match your AccountStatus enum: ACTIVE, INACTIVE, etc.

-- ============================================
-- VERIFY SEED DATA
-- ============================================
-- Run these queries to verify the seed data was inserted correctly

-- SELECT * FROM _user;
-- SELECT * FROM menu;
-- SELECT * FROM pointing_system;
-- SELECT * FROM merchant;
-- SELECT * FROM contact;
-- SELECT * FROM lead;
