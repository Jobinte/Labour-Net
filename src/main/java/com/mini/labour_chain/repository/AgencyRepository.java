package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.Agency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AgencyRepository extends JpaRepository<Agency, Long> {
    Agency findByLicenseNumber(String licenseNumber);
    Agency findByUsername(String username);
}
