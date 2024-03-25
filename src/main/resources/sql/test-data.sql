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
INSERT INTO category (name, priority)
VALUES ('Долг', 1),
       ('Другое', 2),
       ('Квартплата', 3),
       ('Развлечения', 4),
       ('Связь', 5);

INSERT INTO category (name, priority)
VALUES ('Долг', 1),
       ('Другое', 2),
       ('Квартплата', 3),
       ('Развлечения', 4),
       ('Связь', 5);

INSERT INTO category (name, priority)
VALUES ('Долг', 1),
       ('Другое', 2),
       ('Квартплата', 3),
       ('Развлечения', 4),
       ('Связь', 5);

INSERT INTO profile (user_id)
VALUES ('admin');

INSERT INTO profile (user_id)
VALUES ('nastysha');

INSERT INTO profile (user_id)
VALUES ('alexs');

-- Insert category-profile mappings for admin
INSERT INTO profile_categories (profile_id, categories_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
       (2, 6), (2, 7), (2, 8), (2, 9), (2, 10),
       (3, 11), (3, 12), (3, 13), (3, 14), (3, 15);

INSERT INTO payment (name, amount, category_id, user_id, date_time)
SELECT 'Payment ' || ((i - 1) * 10 + c.priority), ROUND((RANDOM() * 1000 + 1)::numeric, 2::int), c.id, 'admin', NOW() - INTERVAL '1 day' * i
FROM generate_series(1::int, (FLOOR(RANDOM() * 100))::bigint) i, category c
WHERE c.id IN (SELECT categories_id FROM profile_categories WHERE profile_id = 1);

INSERT INTO payment (name, amount, category_id, user_id, date_time)
SELECT 'Payment ' || ((i - 1) * 10 + c.priority), ROUND((RANDOM() * 1000 + 1)::numeric, 2::int), c.id, 'nastysha', NOW() - INTERVAL '1 day' * i
FROM generate_series(1::int, (FLOOR(RANDOM() * 100))::bigint) i, category c
WHERE c.id IN (SELECT categories_id FROM profile_categories WHERE profile_id = 2);

INSERT INTO payment (name, amount, category_id, user_id, date_time)
SELECT 'Payment ' || ((i - 1) * 10 + c.priority), ROUND((RANDOM() * 1000 + 1)::numeric, 2::int), c.id, 'nastysha', NOW() - INTERVAL '1 day' * i
FROM generate_series(1::int, (FLOOR(RANDOM() * 100))::bigint) i, category c
WHERE c.id IN (SELECT categories_id FROM profile_categories WHERE profile_id = 3);