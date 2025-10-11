package com.mini.labour_chain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "job_application")
public class JobApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "worker_id")
    private User worker;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String status; // e.g., "Pending", "Approved", "Rejected"

    @Column(columnDefinition = "TEXT")
    private String reply; // Agency's reply to the worker

    // New fields used by JobApplicationController
    private String confirmationStatus; // e.g., "Confirmed"

    private Integer agencyRating; // rating given by worker to agency

    @Column(columnDefinition = "TEXT")
    private String agencyFeedback; // feedback given by worker to agency

    // New fields for agency rating workers
    private Integer workerRating; // rating given by agency to worker (1-5 stars)

    @Column(columnDefinition = "TEXT")
    private String workerFeedback; // feedback given by agency to worker

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Job getJob() { return job; }
    public void setJob(Job job) { this.job = job; }

    public User getWorker() { return worker; }
    public void setWorker(User worker) { this.worker = worker; }

    public String getCoverLetter() { return coverLetter; }
    public void setCoverLetter(String coverLetter) { this.coverLetter = coverLetter; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public String getConfirmationStatus() { return confirmationStatus; }
    public void setConfirmationStatus(String confirmationStatus) { this.confirmationStatus = confirmationStatus; }

    public Integer getAgencyRating() { return agencyRating; }
    public void setAgencyRating(Integer agencyRating) { this.agencyRating = agencyRating; }

    public String getAgencyFeedback() { return agencyFeedback; }
    public void setAgencyFeedback(String agencyFeedback) { this.agencyFeedback = agencyFeedback; }

    public Integer getWorkerRating() { return workerRating; }
    public void setWorkerRating(Integer workerRating) { this.workerRating = workerRating; }

    public String getWorkerFeedback() { return workerFeedback; }
    public void setWorkerFeedback(String workerFeedback) { this.workerFeedback = workerFeedback; }
}

