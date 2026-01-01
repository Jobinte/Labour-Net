package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.*;
import com.mini.labour_chain.repository.JobApplicationRepository;
import com.mini.labour_chain.repository.JobRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
    public String listJobs(Model model,
                           HttpSession session,
                           @RequestParam(value = "q", required = false) String q,
                           @RequestParam(value = "location", required = false) String location,
                           @RequestParam(value = "sort", required = false, defaultValue = "newest") String sort,
                           @RequestParam(value = "page", defaultValue = "0") int page,
                           @RequestParam(value = "size", defaultValue = "8") int size) {

        Sort sortSpec = switch (sort == null ? "newest" : sort) {
            case "salaryAsc" -> Sort.by(Sort.Direction.ASC, "salary");
            case "salaryDesc" -> Sort.by(Sort.Direction.DESC, "salary");
            case "oldest" -> Sort.by(Sort.Direction.ASC, "id");
            default -> Sort.by(Sort.Direction.DESC, "id"); // newest
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1), sortSpec);
        Page<Job> result = jobRepository.searchOpen(
                (q == null || q.isBlank()) ? null : q,
                (location == null || location.isBlank()) ? null : location,
                null,
                null,
                pageable
        );

        model.addAttribute("jobs", result.getContent());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("currentPage", result.getNumber());
        model.addAttribute("pageSize", result.getSize());

        // Keep filter values in the model to persist in the UI
        model.addAttribute("q", q);
        model.addAttribute("location", location);
        model.addAttribute("sort", sort);

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
        job.setSalaryPeriod(updatedJob.getSalaryPeriod());
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
