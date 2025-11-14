package com.phegondev.InventoryMgtSystem;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "Shivam123";
        String hashedPassword = encoder.encode(password);
        System.out.println("Password: " + password);
        System.out.println("BCrypt Hash: " + hashedPassword);
        System.out.println("\nSQL Query:");
        System.out.println("INSERT INTO users (name, email, password, phone_number, role, created_at)");
        System.out.println("VALUES (");
        System.out.println("    'lal5hivam',");
        System.out.println("    'l.singhshivam1@gmail.com',");
        System.out.println("    '" + hashedPassword + "',");
        System.out.println("    '98659458266',");
        System.out.println("    'ADMIN',");
        System.out.println("    NOW()");
        System.out.println(");");
    }
}
