package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.Agency;
import com.mini.labour_chain.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long> {

    // DEFINITIVE FIX: Eagerly fetch the Agency data to prevent LazyInitializationException
    @Query("SELECT j FROM Job j JOIN FETCH j.agency WHERE j.agency = :agency")
    List<Job> findByAgency(@Param("agency") Agency agency);

    @Query("SELECT j FROM Job j JOIN FETCH j.agency")
    List<Job> findAllWithAgency();

    @Query("SELECT j FROM Job j JOIN FETCH j.agency WHERE j.id = :id")
    Optional<Job> findByIdWithAgency(@Param("id") Long id);
}
