package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.*;
import com.mini.labour_chain.repository.JobApplicationRepository;
import com.mini.labour_chain.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/jobs")
public class JobController {

    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;

    @Autowired
    public JobController(JobRepository jobRepository, JobApplicationRepository jobApplicationRepository) {
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public String listJobs(Model model, HttpSession session) {
        model.addAttribute("jobs", jobRepository.findAllWithAgency().stream()
                .filter(job -> "OPEN".equals(job.getStatus()))
                .collect(Collectors.toList()));

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser != null) {
            List<JobApplication> userApplications = jobApplicationRepository.findByWorkerIdWithDetails(loggedInUser.getId());
            model.addAttribute("appliedJobIds", userApplications.stream()
                    .map(app -> app.getJob().getId())
                    .collect(Collectors.toSet()));
        } else {
            model.addAttribute("appliedJobIds", Collections.emptySet());
        }

        return "jobs";
    }

    @GetMapping("/post")
    public String showPostJobForm(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAgency") == null) {
            return "redirect:/agencies/login";
        }
        model.addAttribute("job", new Job());
        return "job_post";
    }

    @PostMapping("/post")
    public String postJob(@ModelAttribute Job job, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        job.setAgency(loggedInAgency);
        job.setStatus("OPEN");
        jobRepository.save(job);

        redirectAttributes.addFlashAttribute("popupMessage", "✅ Job posted successfully!");
        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/edit/{id}")
    @Transactional(readOnly = true)
    public String showEditJobForm(@PathVariable Long id, Model model, HttpSession session) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        Job job = jobRepository.findByIdWithAgency(id).orElse(null);
        if (job == null || !job.getAgency().equals(loggedInAgency)) {
            return "redirect:/agencies/dashboard";
        }

        model.addAttribute("job", job);
        return "job_edit";
    }

    @PostMapping("/edit/{id}")
    public String editJob(@PathVariable Long id, @ModelAttribute Job updatedJob, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        Job job = jobRepository.findById(id).orElse(null);
        if (job == null || !job.getAgency().equals(loggedInAgency)) {
            return "redirect:/agencies/dashboard";
        }

        job.setTitle(updatedJob.getTitle());
        job.setDescription(updatedJob.getDescription());
        job.setLocation(updatedJob.getLocation());
        job.setSalary(updatedJob.getSalary());
        jobRepository.save(job);

        redirectAttributes.addFlashAttribute("popupMessage", "✅ Job updated successfully!");
        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/delete/{id}")
    @Transactional
    public String deleteJob(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        Job job = jobRepository.findById(id).orElse(null);
        if (job != null && job.getAgency().equals(loggedInAgency)) {
            jobApplicationRepository.deleteByJob(job);
            jobRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("popupMessage", "🗑️ Job deleted successfully!");
        }

        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/toggle-status/{id}")
    public String toggleJobStatus(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        Job job = jobRepository.findById(id).orElse(null);
        if (job != null && job.getAgency().equals(loggedInAgency)) {
            if ("OPEN".equals(job.getStatus())) {
                job.setStatus("CLOSED");
                redirectAttributes.addFlashAttribute("popupMessage", "Recruiting for this job has been stopped.");
            } else {
                job.setStatus("OPEN");
                redirectAttributes.addFlashAttribute("popupMessage", "Recruiting for this job has been re-opened.");
            }
            jobRepository.save(job);
        }

        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/apply/{jobId}")
    @Transactional(readOnly = true)
    public String showApplicationForm(@PathVariable Long jobId, HttpSession session, Model model) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/workers/login";
        }

        Job job = jobRepository.findByIdWithAgency(jobId).orElse(null);
        if (job == null || !"OPEN".equals(job.getStatus())) {
            return "redirect:/jobs";
        }

        model.addAttribute("job", job);
        model.addAttribute("worker", loggedInUser);
        return "job_apply";
    }

    @PostMapping("/apply/{jobId}")
    public String submitApplication(@PathVariable Long jobId,
                                    @RequestParam String coverLetter,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/workers/login";
        }

        Job job = jobRepository.findById(jobId).orElse(null);
        if (job == null || !"OPEN".equals(job.getStatus())) {
            return "redirect:/jobs";
        }

        List<JobApplication> existingApplications = jobApplicationRepository.findByWorkerIdWithDetails(loggedInUser.getId());
        boolean alreadyApplied = existingApplications.stream()
                .anyMatch(app -> app.getJob().getId().equals(jobId));

        if (alreadyApplied) {
            redirectAttributes.addFlashAttribute("popupMessage", "You have already applied for this job!");
            return "redirect:/jobs";
        }

        JobApplication application = new JobApplication();
        application.setJob(job);
        application.setWorker(loggedInUser);
        application.setCoverLetter(coverLetter);
        application.setStatus("Pending");

        jobApplicationRepository.save(application);

        return "redirect:/workers/dashboard?message=application_submitted";
    }
}
