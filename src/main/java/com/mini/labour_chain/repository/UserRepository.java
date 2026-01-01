package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByAadharNumber(String aadharNumber);
    User findByUsername(String username);
}
