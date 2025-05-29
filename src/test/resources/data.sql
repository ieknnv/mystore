INSERT INTO users (name, email)
VALUES ('Ilia','ilia@mail.ru');

INSERT INTO carts (user_id)
VALUES (1);

INSERT INTO items (name, description, item_image, price)
VALUES ('Item 1', 'Test item 1 with keyword', X'', 5.0),
       ('Item 2', 'Test item 2', X'', 6.0),
       ('Item 3', 'Test item 3', X'', 7.0),
       ('Item 4', 'Test item 4 with keyword', X'', 8.0);