package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.Agency;
import com.mini.labour_chain.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    @Query(
            value = "SELECT j FROM Job j JOIN FETCH j.agency a " +
                    "WHERE j.status = 'OPEN' " +
                    "AND (:q IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                    "AND (:loc IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :loc, '%'))) " +
                    "AND (:minSalary IS NULL OR j.salary >= :minSalary) " +
                    "AND (:maxSalary IS NULL OR j.salary <= :maxSalary)",
            countQuery = "SELECT COUNT(j) FROM Job j " +
                    "WHERE j.status = 'OPEN' " +
                    "AND (:q IS NULL OR LOWER(j.title) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(j.description) LIKE LOWER(CONCAT('%', :q, '%'))) " +
                    "AND (:loc IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :loc, '%'))) " +
                    "AND (:minSalary IS NULL OR j.salary >= :minSalary) " +
                    "AND (:maxSalary IS NULL OR j.salary <= :maxSalary)"
    )
    Page<Job> searchOpen(
            @Param("q") String q,
            @Param("loc") String location,
            @Param("minSalary") Double minSalary,
            @Param("maxSalary") Double maxSalary,
            Pageable pageable
    );
}
