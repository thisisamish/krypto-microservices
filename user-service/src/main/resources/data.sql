-- Single-role column version
INSERT INTO users (email, username, password, user_role, is_super_admin)
VALUES ('alice@example.com', 'alice', '{noop}secret', 'CUSTOMER', FALSE);
