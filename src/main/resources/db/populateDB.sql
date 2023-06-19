TRUNCATE TABLE user_role CASCADE;
TRUNCATE TABLE users CASCADE;
TRUNCATE TABLE meals CASCADE;

ALTER SEQUENCE global_seq RESTART WITH 100000;

INSERT INTO users (name, email, password)
VALUES ('User', 'user@yandex.ru', 'password'),
       ('Admin', 'admin@gmail.com', 'admin'),
       ('Guest', 'guest@gmail.com', 'guest');

INSERT INTO user_role (role, user_id)
VALUES ('USER', 100000),
       ('ADMIN', 100001);

INSERT INTO meals (user_id, date_time, description, calories)
VALUES
    (100000, '2023-06-16 12:00:00', 'Breakfast', 500),
    (100000, '2023-06-16 13:30:00', 'Lunch', 800),
    (100001, '2023-06-16 18:00:00', 'Dinner', 700),
    (100001, '2023-06-17 08:30:00', 'Breakfast', 400),
    (100001, '2023-06-17 13:00:00', 'Lunch', 600);
