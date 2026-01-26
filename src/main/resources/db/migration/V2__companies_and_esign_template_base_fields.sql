CREATE TABLE company (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    city VARCHAR(255),
    state VARCHAR(255),
    zip_code VARCHAR(255),
    country VARCHAR(255),
    phone VARCHAR(255),
    email VARCHAR(255),
    website VARCHAR(255),
    description VARCHAR(1000),
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_by VARCHAR(255) NOT NULL,
    last_updated_by VARCHAR(255),
    CONSTRAINT fk_company_user FOREIGN KEY (user_id) REFERENCES _user(id)
);

ALTER TABLE template_default
    ADD COLUMN company_id BIGINT NOT NULL,
    ADD CONSTRAINT fk_template_default_company FOREIGN KEY (company_id) REFERENCES company(id);

ALTER TABLE esign_template
    ADD COLUMN base_fields JSONB;
