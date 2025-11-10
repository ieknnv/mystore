INSERT INTO users (username, email, password_hash, roles, enabled)
VALUES ('user1','user1@mail.ru','$2a$12$MBw1SDhCmSXYQJOkOKahtOM6ztuLCoxVYytrfNlF70zUO6HkokXl.','ROLE_USER',true),
       ('user2','user2@mail.ru','$2a$12$E5Y2jdrYPPjCaUSWE9FTRuNf9OxzWwLUZEhNAzfhTt8jOlYzZfgOq','ROLE_USER',true),
       ('admin','admin2@mail.ru','$2a$12$oOQT6S6.wdikEDzX859UM.cagTrhPEi0ODIVE9qdnS5TjxmmPjaPm','ROLE_ADMIN',true);

INSERT INTO carts (user_id)
VALUES ((SELECT id FROM users WHERE username = 'user1')),
       ((SELECT id FROM users WHERE username = 'user2'));