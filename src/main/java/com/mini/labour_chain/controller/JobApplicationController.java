package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.JobApplication;
import com.mini.labour_chain.model.User;
import com.mini.labour_chain.repository.JobApplicationRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/applications")
public class JobApplicationController {

    private final JobApplicationRepository jobApplicationRepository;

    @Autowired
    public JobApplicationController(JobApplicationRepository jobApplicationRepository) {
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @GetMapping("/worker/{workerId}")
    @Transactional(readOnly = true)
    public String getWorkerApplications(@PathVariable Long workerId,
                                        HttpSession session,
                                        Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null || !sessionUser.getId().equals(workerId)) {
            return "redirect:/workers/login";
        }

        // DEFINITIVE FIX: Use the correct repository method with the correct parameter type (Long).
        List<JobApplication> applications = jobApplicationRepository.findByWorkerIdWithDetails(sessionUser.getId());
        model.addAttribute("applications", applications);
        return "my_applications";
    }

    @GetMapping("/confirm/{applicationId}")
    public String confirmJob(@PathVariable Long applicationId, HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/workers/login";
        }

        JobApplication application = jobApplicationRepository.findById(applicationId).orElse(null);
        if (application != null && application.getWorker().equals(loggedInUser) && "Approved".equals(application.getStatus())) {
            application.setConfirmationStatus("Confirmed");
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "✅ Job confirmed successfully! You can now rate the agency.");
        }

        return "redirect:/applications/worker/" + loggedInUser.getId();
    }

    @PostMapping("/rate-agency/{applicationId}")
    public String rateAgency(@PathVariable Long applicationId,
                             @RequestParam Integer rating,
                             @RequestParam String feedback,
                             HttpSession session, RedirectAttributes redirectAttributes) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/workers/login";
        }

        JobApplication application = jobApplicationRepository.findById(applicationId).orElse(null);
        if (application != null && application.getWorker().equals(loggedInUser) && "Confirmed".equals(application.getConfirmationStatus())) {
            application.setAgencyRating(rating);
            application.setAgencyFeedback(feedback);
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "Thank you for your feedback!");
        }

        return "redirect:/applications/worker/" + loggedInUser.getId();
    }
}
