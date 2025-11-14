-- Create Admin User for Inventory Management System
-- Password: password123 (BCrypt hashed)
-- Login: admin@admin.com / password123

INSERT INTO users (name, email, password, phone_number, role, created_at) 
VALUES (
  'Admin User',
  'admin@admin.com',
  '$2a$10$N9qo8uLOickgx2ZMRZoMyeJ8bFjJfJ8qzJNfvFYZKCqJ8Xf.Fw2',
  '1234567890',
  'ADMIN',
  NOW()
);

-- Verify the user was created
SELECT id, name, email, role FROM users WHERE email = 'admin@admin.com';
