USE socius_db;

ALTER TABLE posts
    MODIFY post_type ENUM('text', 'resource', 'event', 'image') DEFAULT 'text',
    ADD COLUMN image_url VARCHAR(500) AFTER resource_url,
    ADD COLUMN image_alt_text VARCHAR(255) AFTER image_url;
