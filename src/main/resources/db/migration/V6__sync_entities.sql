ALTER TABLE document
    ADD COLUMN declined_nr BIGINT DEFAULT 0;

ALTER TABLE recipient
    ADD COLUMN status VARCHAR(50) NOT NULL DEFAULT 'PENDING';

ALTER TABLE recipient
    ADD COLUMN token VARCHAR(32);

UPDATE recipient
SET token = md5(random()::text || clock_timestamp()::text || id::text)
WHERE token IS NULL;

ALTER TABLE recipient
    ALTER COLUMN token SET NOT NULL;

ALTER TABLE recipient
    ADD CONSTRAINT uk_recipient_token UNIQUE (token);
