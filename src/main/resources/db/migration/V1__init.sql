CREATE TABLE _user (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    email VARCHAR(255),
    name VARCHAR(255),
    last_name VARCHAR(255),
    password VARCHAR(255),
    phone VARCHAR(255),
    company_name VARCHAR(255),
    job_title VARCHAR(255),
    account_status VARCHAR(50),
    role VARCHAR(50)
);

CREATE TABLE token (
    id SERIAL PRIMARY KEY,
    token VARCHAR(255),
    token_type VARCHAR(50),
    revoked BOOLEAN NOT NULL,
    expired BOOLEAN NOT NULL,
    user_id BIGINT,
    CONSTRAINT uk_token_token UNIQUE (token),
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE menu (
    id BIGSERIAL PRIMARY KEY,
    label VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

CREATE TABLE merchant (
    id BIGSERIAL PRIMARY KEY,
    joined_miles BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

CREATE TABLE contact (
    id BIGSERIAL PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

CREATE TABLE lead (
    id BIGSERIAL PRIMARY KEY,
    is_signed BOOLEAN,
    is_active BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255)
);

CREATE TABLE pointing_system (
    id BIGSERIAL PRIMARY KEY,
    plan VARCHAR(50),
    pricing_type VARCHAR(255),
    card_fee REAL,
    bps REAL
);

CREATE TABLE template (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    menu_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_template_user FOREIGN KEY (user_id) REFERENCES _user(id),
    CONSTRAINT fk_template_menu FOREIGN KEY (menu_id) REFERENCES menu(id)
);

CREATE TABLE template_default (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    menu_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_template_default_user FOREIGN KEY (user_id) REFERENCES _user(id),
    CONSTRAINT fk_template_default_menu FOREIGN KEY (menu_id) REFERENCES menu(id)
);

CREATE TABLE template_form (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    key VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    options JSONB,
    form_props JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT uk_template_form_key UNIQUE (key),
    CONSTRAINT fk_template_form_template FOREIGN KEY (template_id) REFERENCES template(id)
);

CREATE TABLE template_form_default (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL,
    key VARCHAR(255) NOT NULL,
    label VARCHAR(255) NOT NULL,
    type VARCHAR(50),
    options JSONB,
    form_props JSONB,
    column_props JSONB,
    search_contact BOOLEAN,
    search_merchant BOOLEAN,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT uk_template_form_default_key UNIQUE (key),
    CONSTRAINT fk_template_form_default_template FOREIGN KEY (template_id) REFERENCES template_default(id)
);

CREATE TABLE template_form_value (
    id BIGSERIAL PRIMARY KEY,
    template_form_id BIGINT NOT NULL,
    record_id BIGINT NOT NULL,
    value VARCHAR(255),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_template_form_value_template_form FOREIGN KEY (template_form_id) REFERENCES template_form(id)
);

CREATE TABLE template_form_value_default (
    id BIGSERIAL PRIMARY KEY,
    template_form_default_id BIGINT NOT NULL,
    record_id BIGINT NOT NULL,
    value VARCHAR(255),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_template_form_value_default_template_form_default FOREIGN KEY (template_form_default_id) REFERENCES template_form_default(id),
    CONSTRAINT fk_template_form_value_default_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE merchant_tpl (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT,
    tpl VARCHAR(255),
    CONSTRAINT fk_merchant_tpl_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE contact_merchant (
    id BIGSERIAL PRIMARY KEY,
    contact_id BIGINT,
    merchant_id BIGINT,
    CONSTRAINT fk_contact_merchant_contact FOREIGN KEY (contact_id) REFERENCES contact(id),
    CONSTRAINT fk_contact_merchant_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE merchant_miles (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT,
    points BIGINT,
    pointing_system_id BIGINT,
    user_id BIGINT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT uk_merchant_miles_merchant UNIQUE (merchant_id),
    CONSTRAINT fk_merchant_miles_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id),
    CONSTRAINT fk_merchant_miles_pointing_system FOREIGN KEY (pointing_system_id) REFERENCES pointing_system(id),
    CONSTRAINT fk_merchant_miles_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

CREATE TABLE dejavoo_credentials (
    id BIGSERIAL PRIMARY KEY,
    merchant_id BIGINT,
    register_id VARCHAR(255),
    auth_key VARCHAR(255),
    api_url VARCHAR(255),
    is_active BOOLEAN,
    CONSTRAINT uk_dejavoo_credentials_merchant UNIQUE (merchant_id),
    CONSTRAINT fk_dejavoo_credentials_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE merchant_dejavoo_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    password VARCHAR(255) NOT NULL,
    password_text VARCHAR(255),
    merchant_id BIGINT,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP,
    CONSTRAINT uk_merchant_dejavoo_users_username UNIQUE (username),
    CONSTRAINT uk_merchant_dejavoo_users_merchant UNIQUE (merchant_id),
    CONSTRAINT fk_merchant_dejavoo_users_merchant FOREIGN KEY (merchant_id) REFERENCES merchant(id)
);

CREATE TABLE file_metadata (
    id BIGSERIAL PRIMARY KEY,
    original_filename VARCHAR(255),
    stored_filename VARCHAR(255),
    file_path VARCHAR(255),
    file_size BIGINT,
    content_type VARCHAR(255),
    cloud_provider VARCHAR(255),
    bucket_name VARCHAR(255),
    file_url VARCHAR(255),
    uploaded_by BIGINT,
    uploaded_at TIMESTAMP,
    is_public BOOLEAN,
    CONSTRAINT fk_file_metadata_uploaded_by FOREIGN KEY (uploaded_by) REFERENCES _user(id)
);

CREATE TABLE esign_template (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100),
    description VARCHAR(255),
    file_metadata_id BIGINT,
    fields JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT uk_esign_template_name UNIQUE (name),
    CONSTRAINT uk_esign_template_file_metadata UNIQUE (file_metadata_id),
    CONSTRAINT fk_esign_template_file_metadata FOREIGN KEY (file_metadata_id) REFERENCES file_metadata(id)
);

CREATE TABLE document (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    file_metadata_id BIGINT,
    nr_of_recipient BIGINT,
    signed_nr BIGINT,
    esign_template_id BIGINT,
    fields JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT uk_document_file_metadata UNIQUE (file_metadata_id),
    CONSTRAINT fk_document_file_metadata FOREIGN KEY (file_metadata_id) REFERENCES file_metadata(id),
    CONSTRAINT fk_document_esign_template FOREIGN KEY (esign_template_id) REFERENCES esign_template(id)
);

CREATE TABLE recipient (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    document_id BIGINT,
    recipient_fields JSONB,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_recipient_document FOREIGN KEY (document_id) REFERENCES document(id)
);

CREATE TABLE signed_url_cache (
    id BIGSERIAL PRIMARY KEY,
    file_metadata_id BIGINT UNIQUE,
    signed_url TEXT,
    created_at TIMESTAMP NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_signed_url_cache_file_metadata FOREIGN KEY (file_metadata_id) REFERENCES file_metadata(id)
);

CREATE INDEX idx_file_metadata_id ON signed_url_cache(file_metadata_id);
