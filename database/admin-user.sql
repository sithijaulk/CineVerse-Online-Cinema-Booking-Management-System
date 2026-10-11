-- Admin credentials
-- Email: admin@cinema.com
-- Password: password

INSERT INTO users (
    full_name,
    email,
    password,
    phone,
    role,
    status,
    created_at,
    updated_at
)
VALUES (
    'System Administrator',
    'admin@cinema.com',
    '$2a$10$VxPCLDkv8Qa9tLC.IrL9Re2ANDtpcIoHAVBYmvvXHtBBw4zIddGkW',
    NULL,
    'ADMIN',
    'ACTIVE',
    NOW(),
    NOW()
)
ON DUPLICATE KEY UPDATE
    full_name = VALUES(full_name),
    password = VALUES(password),
    role = 'ADMIN',
    status = 'ACTIVE',
    updated_at = NOW();