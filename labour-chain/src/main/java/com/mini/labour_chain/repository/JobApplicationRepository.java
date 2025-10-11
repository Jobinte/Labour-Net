package com.mini.labour_chain.repository;

import com.mini.labour_chain.model.Job;
import com.mini.labour_chain.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    
    void deleteByJob(Job job);
    List<JobApplication> findByJobIn(List<Job> jobs);
    void deleteByWorker(com.mini.labour_chain.model.User worker);
    List<JobApplication> findByWorker(com.mini.labour_chain.model.User worker);

    // DEFINITIVE FIX: Eagerly fetch all related entities to prevent LazyInitializationException
    @Query("SELECT ja FROM JobApplication ja LEFT JOIN FETCH ja.job j LEFT JOIN FETCH j.agency WHERE ja.worker.id = :workerId")
    List<JobApplication> findByWorkerIdWithDetails(@Param("workerId") Long workerId);

    @Query("SELECT ja FROM JobApplication ja LEFT JOIN FETCH ja.job j LEFT JOIN FETCH ja.worker WHERE ja.job.agency.id = :agencyId")
    List<JobApplication> findByAgencyIdWithDetails(@Param("agencyId") Long agencyId);
}
