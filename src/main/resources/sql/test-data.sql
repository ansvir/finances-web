CALL check_users_exist();


INSERT INTO app_user (username, password, enabled)
VALUES ('admin', '$2a$12$MPR1fNKX8fSeuTbm7h/0EOpAAc//UiURywJJqTz/0wSBh3VVqqUsG', true),
       ('nastysha', '$2a$12$hx9AfH.SCvjIo.u2nPyGiOsFtp5IeIE1v0ocGJW675.K3iCSH0cgu', true);

INSERT INTO groups VALUES (1, 'USER');

INSERT INTO authorities VALUES ('admin', 'USER');
INSERT INTO authorities VALUES ('nastysha', 'USER');

INSERT INTO group_authorities VALUES (1, 'USER');

-- Insert categories
INSERT INTO category (name, priority)
VALUES ('Долг', 1),
       ('Другое', 2),
       ('Квартплата', 3),
       ('Развлечения', 4),
       ('Связь', 5);

INSERT INTO category (name, priority)
VALUES ('Долг', 6),
       ('Другое', 7),
       ('Квартплата', 8),
       ('Развлечения', 9),
       ('Связь', 10);

-- Insert profile for admin
INSERT INTO profile (user_id)
VALUES ('admin');

-- Insert profile for nastysha
INSERT INTO profile (user_id)
VALUES ('nastysha');

-- Insert category-profile mappings for admin
INSERT INTO profile_categories (profile_id, categories_id)
VALUES (1, 1), (1, 2), (1, 3), (1, 4), (1, 5),
       (2, 6), (2, 7), (2, 8), (2, 9), (2, 10);

-- Generate and insert payments for admin
INSERT INTO payment (name, amount, category_id, user_id, date_time)
SELECT 'Payment ' || ((i - 1) * 10 + c.priority), ROUND((RANDOM() * 1000 + 1)::numeric, 2::int), c.id, 'admin', NOW() - INTERVAL '1 month' * i
FROM generate_series(1::int, (FLOOR(RANDOM() * 100))::bigint) i, category c
WHERE c.id IN (SELECT categories_id FROM profile_categories WHERE profile_id = 1);

-- Generate and insert payments for nastysha
INSERT INTO payment (name, amount, category_id, user_id, date_time)
SELECT 'Payment ' || ((i - 1) * 10 + c.priority), ROUND((RANDOM() * 1000 + 1)::numeric, 2::int), c.id, 'nastysha', NOW() - INTERVAL '1 month' * i
FROM generate_series(1::int, (FLOOR(RANDOM() * 100))::bigint) i, category c
WHERE c.id IN (SELECT categories_id FROM profile_categories WHERE profile_id = 2);