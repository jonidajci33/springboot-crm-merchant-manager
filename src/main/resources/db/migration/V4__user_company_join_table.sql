CREATE TABLE user_company (
    user_id BIGINT NOT NULL,
    company_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, company_id),
    CONSTRAINT fk_user_company_user FOREIGN KEY (user_id) REFERENCES _user(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_company_company FOREIGN KEY (company_id) REFERENCES company(id) ON DELETE CASCADE
);

INSERT INTO user_company (user_id, company_id)
SELECT user_id, id
FROM company;

ALTER TABLE company DROP CONSTRAINT IF EXISTS fk_company_user;
ALTER TABLE company DROP COLUMN IF EXISTS user_id;
