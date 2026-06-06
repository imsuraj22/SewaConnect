-- Run once against ngo_service_db (local: psql -U postgres -h localhost -d ngo_service_db -f this-file.sql)
-- Fixes NOT NULL + unique constraints on empty profile placeholders.

ALTER TABLE IF EXISTS ngo ALTER COLUMN address DROP NOT NULL;
ALTER TABLE IF EXISTS ngo ALTER COLUMN name DROP NOT NULL;
ALTER TABLE IF EXISTS ngo ALTER COLUMN description DROP NOT NULL;
ALTER TABLE IF EXISTS ngo ALTER COLUMN email DROP NOT NULL;
ALTER TABLE IF EXISTS ngo ALTER COLUMN phone_number DROP NOT NULL;

ALTER TABLE IF EXISTS ngo DROP COLUMN IF EXISTS location_lat;
ALTER TABLE IF EXISTS ngo DROP COLUMN IF EXISTS location_lng;

UPDATE ngo SET email = contact_email WHERE email IS NULL AND contact_email IS NOT NULL;

-- Empty strings collide on legacy UNIQUE indexes; use NULL until profile is completed
UPDATE ngo SET name = NULL WHERE name IS NOT NULL AND trim(name) = '';
UPDATE ngo SET address = NULL WHERE address IS NOT NULL AND trim(address) = '';
UPDATE ngo SET description = NULL WHERE description IS NOT NULL AND trim(description) = '';
UPDATE ngo SET email = NULL WHERE email IS NOT NULL AND trim(email) = '';
UPDATE ngo SET phone_number = NULL WHERE phone_number IS NOT NULL AND trim(phone_number) = '';
UPDATE ngo SET contact_email = NULL WHERE contact_email IS NOT NULL AND trim(contact_email) = '';

-- Known Hibernate-generated unique constraints (names vary by environment)
ALTER TABLE IF EXISTS ngo DROP CONSTRAINT IF EXISTS uk_xtaobj87uatt5ndevo03xrdn;
ALTER TABLE IF EXISTS ngo DROP CONSTRAINT IF EXISTS uk_q4x6pq8r1d40ijbafqjgwppls;

-- Drop any remaining UNIQUE constraints on ngo (except primary key)
DO $$
DECLARE r RECORD;
BEGIN
  FOR r IN
    SELECT c.conname
    FROM pg_constraint c
    JOIN pg_class t ON c.conrelid = t.oid
    WHERE t.relname = 'ngo' AND c.contype = 'u'
  LOOP
    EXECUTE format('ALTER TABLE ngo DROP CONSTRAINT IF EXISTS %I', r.conname);
  END LOOP;
END $$;

ALTER TABLE IF EXISTS ngo ADD COLUMN IF NOT EXISTS phone_number VARCHAR(255);
ALTER TABLE IF EXISTS ngo ADD COLUMN IF NOT EXISTS contact_email VARCHAR(255);

-- One NGO profile per user (matches entity uk_ngo_user_id)
DELETE FROM ngo a
USING ngo b
WHERE a.user_id IS NOT NULL
  AND a.user_id = b.user_id
  AND a.id > b.id;

CREATE UNIQUE INDEX IF NOT EXISTS uk_ngo_user_id ON ngo (user_id);

-- Store file bytes as bytea (not PostgreSQL OID/large objects) to avoid LOB auto-commit errors
-- Table name follows Spring Boot default (NGODocument -> ngo_document)
ALTER TABLE IF EXISTS ngo_document
    ALTER COLUMN document_data TYPE bytea
    USING CASE
        WHEN document_data IS NULL THEN NULL
        ELSE document_data::bytea
    END;

ALTER TABLE IF EXISTS ngo_images
    ALTER COLUMN image_data TYPE bytea
    USING CASE
        WHEN image_data IS NULL THEN NULL
        ELSE image_data::bytea
    END;

-- package_images.image: Hibernate @Lob used OID; app now uses bytea (PackageImage entity)
ALTER TABLE IF EXISTS package_images
    ALTER COLUMN image TYPE bytea
    USING CASE
        WHEN image IS NULL THEN NULL
        ELSE lo_get(image)
    END;
