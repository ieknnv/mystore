INSERT INTO users (name, email)
VALUES ('Ilia','ilia@mail.ru');

INSERT INTO carts (user_id)
VALUES ((SELECT id FROM users WHERE name = 'Ilia'));