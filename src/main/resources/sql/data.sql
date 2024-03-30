TRUNCATE TABLE profile_categories CASCADE;
TRUNCATE TABLE profile CASCADE;
TRUNCATE TABLE category CASCADE;
TRUNCATE TABLE app_user CASCADE;
TRUNCATE TABLE groups CASCADE;
TRUNCATE TABLE authorities CASCADE;
TRUNCATE TABLE group_authorities CASCADE;

INSERT INTO app_user (username, password, enabled)
VALUES ('admin', '$2a$12$MPR1fNKX8fSeuTbm7h/0EOpAAc//UiURywJJqTz/0wSBh3VVqqUsG', true),
       ('nastysha', '$2a$12$Fq/F5a9qD.5GQTdESTnn5et.goN.I0M7GbP2N0rernO9V8jucQMzu', true),
       ('alexs', '$2a$12$TE7DBzqSrGtUORLEDxcdLeygJT5UX90yVtxKJghRZd.PZg6KZqFKe', true);

INSERT INTO groups VALUES (1, 'USER');

INSERT INTO authorities VALUES ('admin', 'USER');
INSERT INTO authorities VALUES ('nastysha', 'USER');
INSERT INTO authorities VALUES ('alexs', 'USER');

INSERT INTO group_authorities VALUES (1, 'USER');

-- Insert categories
INSERT INTO category
VALUES (1, 'Долг', 1),
       (2, 'Другое', 2),
       (3, 'Квартплата', 3),
       (4, 'Развлечения', 4),
       (5, 'Связь', 5);

INSERT INTO category
VALUES (6, 'Долг', 1),
       (7, 'Другое', 2),
       (8, 'Квартплата', 3),
       (9, 'Развлечения', 4),
       (10, 'Связь', 5);

INSERT INTO category
VALUES (11, 'Долг', 1),
       (12, 'Другое', 2),
       (13, 'Квартплата', 3),
       (14, 'Развлечения', 4),
       (15, 'Связь', 5);

INSERT INTO profile
VALUES (1, 'admin');

INSERT INTO profile
VALUES (2, 'nastysha');

INSERT INTO profile
VALUES (3, 'alexs');

INSERT INTO profile_categories (profile_id, categories_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
       (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
       (3, 11), (3, 12), (3, 13), (3, 14), (3, 15);