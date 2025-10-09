package com.mini.labour_chain.config;

import com.mini.labour_chain.model.*;
import com.mini.labour_chain.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AadharVerificationRepository aadharRepository;

    @Autowired
    private AgencyVerificationRepository agencyVerificationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        initializeAdminData();
        initializeAadharData();
        initializeAgencyVerificationData();
    }

    private void initializeAdminData() {
        if (adminRepository.count() == 0) {
            Admin admin = new Admin();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@labournet.com");
            adminRepository.save(admin);
            System.out.println("✅ Default admin created: username=admin, password=admin123");
            System.out.println("📝 Admin password encoded with BCrypt");
        } else {
            System.out.println("ℹ️ Admin account already exists");
        }
    }

    private void initializeAadharData() {
        if (aadharRepository.count() == 0) {
            // Sample Aadhaar data for testing
            AadharVerification aadhaar1 = new AadharVerification();
            aadhaar1.setAadharNumber("123456789012");
            aadhaar1.setName("John Worker");
            aadhaar1.setAddress("123 Main St, Mumbai");
            aadhaar1.setDob("1990-01-01");
            aadhaar1.setGender("Male");
            aadhaar1.setPhoneNumber("9876543210");
            aadharRepository.save(aadhaar1);

            AadharVerification aadhaar2 = new AadharVerification();
            aadhaar2.setAadharNumber("987654321098");
            aadhaar2.setName("Jane Smith");
            aadhaar2.setAddress("456 Oak Ave, Delhi");
            aadhaar2.setDob("1985-05-15");
            aadhaar2.setGender("Female");
            aadhaar2.setPhoneNumber("8765432109");
            aadharRepository.save(aadhaar2);

            System.out.println("Sample Aadhaar verification data created");
        }
    }

    private void initializeAgencyVerificationData() {
        if (agencyVerificationRepository.count() == 0) {
            // Sample Agency verification data for testing
            AgencyVerification agency1 = new AgencyVerification();
            agency1.setLicenseNumber("LIC123456");
            agency1.setProprietorName("Agency Owner One");
            agency1.setContactNumber("9876543210");
            agency1.setAddress("789 Business St, Bangalore");
            agencyVerificationRepository.save(agency1);

            AgencyVerification agency2 = new AgencyVerification();
            agency2.setLicenseNumber("LIC789012");
            agency2.setProprietorName("Agency Owner Two");
            agency2.setContactNumber("8765432109");
            agency2.setAddress("321 Commerce Rd, Chennai");
            agencyVerificationRepository.save(agency2);

            System.out.println("Sample Agency verification data created");
        }
    }
}

