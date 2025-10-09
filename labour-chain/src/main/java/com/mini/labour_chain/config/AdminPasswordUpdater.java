package com.mini.labour_chain.config;

import com.mini.labour_chain.model.Admin;
import com.mini.labour_chain.repository.AdminRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Order(1) // Run this before DataInitializer
public class AdminPasswordUpdater implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        updateExistingAdminPassword();
    }

    private void updateExistingAdminPassword() {
        // Find admin by username (case-insensitive)
        Admin admin = adminRepository.findByUsername("Admin");
        if (admin == null) {
            admin = adminRepository.findByUsername("admin");
        }
        
        if (admin != null) {
            String currentPassword = admin.getPassword();
            
            // Check if password is already encoded (BCrypt hashes start with $2a$, $2b$, or $2y$)
            if (!currentPassword.startsWith("$2")) {
                System.out.println("🔧 Found admin with plain text password, updating to BCrypt...");
                System.out.println("👤 Admin username: " + admin.getUsername());
                System.out.println("🔑 Old password: " + currentPassword);
                
                // Keep the original password but encode it with BCrypt
                String newEncodedPassword = passwordEncoder.encode(currentPassword);
                admin.setPassword(newEncodedPassword);
                adminRepository.save(admin);
                System.out.println("✅ Admin password encoded with BCrypt (password: '" + currentPassword + "')");
                System.out.println("🔐 Use these credentials:");
                System.out.println("   Username: " + admin.getUsername());
                System.out.println("   Password: " + currentPassword);
            } else {
                System.out.println("ℹ️ Admin password is already BCrypt encoded");
            }
        } else {
            System.out.println("ℹ️ No existing admin found, DataInitializer will create one");
        }
    }
}
