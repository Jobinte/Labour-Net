
package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username);
    Admin findByUsernameIgnoreCase(String username);
}
