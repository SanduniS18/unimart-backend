-- src/main/resources/db/migration/V3__add_active_to_users.sql
ALTER TABLE users
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;
