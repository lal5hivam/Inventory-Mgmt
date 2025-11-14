-- Create Admin User for Inventory Management System
-- Password: password123 (BCrypt hashed)
-- Login: admin@admin.com / password123

-- FIRST: Delete the old admin user if it exists
DELETE FROM users WHERE email = 'admin@admin.com';

-- Create new admin user with correct BCrypt hash
INSERT INTO users (name, email, password, phone_number, role) 
VALUES (
  'Admin User',
  'admin@admin.com',
  '$2a$10$slYQM.8R8yvBL8MZZ5J6XeuF3S9C.0eDHOGrqQqQqX.vjqWQqI0Iq',
  '1234567890',
  'ADMIN'
);

-- Verify the user was created
SELECT id, name, email, role FROM users WHERE email = 'admin@admin.com';
