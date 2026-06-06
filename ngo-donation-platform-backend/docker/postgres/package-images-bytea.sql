-- Run once against ngo_service_db:
--   psql -U postgres -h localhost -d ngo_service_db -f package-images-bytea.sql
--
-- Fixes: column "image" is of type oid but expression is of type bytea

-- If you have no rows yet (or failed inserts only), this is enough:
-- TRUNCATE package_images;

ALTER TABLE IF EXISTS package_images
    ALTER COLUMN image TYPE bytea
    USING CASE
        WHEN image IS NULL THEN NULL
        ELSE lo_get(image)
    END;
