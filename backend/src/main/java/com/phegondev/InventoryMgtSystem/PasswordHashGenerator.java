package com.phegondev.InventoryMgtSystem;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "password123";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        System.out.println("\nSQL Query:");
        System.out.println("INSERT INTO users (name, email, password, phone_number, role)");
        System.out.println("VALUES (");
        System.out.println("    'Admin User',");
        System.out.println("    'admin@admin.com',");
        System.out.println("    '" + hashedPassword + "',");
        System.out.println("    '1234567890',");
        System.out.println("    'ADMIN'");
        System.out.println(");");
    }
}
