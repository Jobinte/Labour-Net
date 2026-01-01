package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.BlacklistEntry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlacklistRepository extends JpaRepository<BlacklistEntry, Long> {
    boolean existsByIdValue(String idValue);
    BlacklistEntry findByIdValue(String idValue);
}
