package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.Admin;
import com.mini.labour_chain.model.Agency;
import com.mini.labour_chain.model.User;
import com.mini.labour_chain.model.BlacklistEntry;
import com.mini.labour_chain.model.Job;
import com.mini.labour_chain.repository.*;
import com.mini.labour_chain.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminRepository adminRepo;
    private final UserRepository userRepo;
    private final AgencyRepository agencyRepo;
    private final JobRepository jobRepo;
    private final JobApplicationRepository jobApplicationRepo;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistRepository blacklistRepo;
    private final MailService mailService;

    @Autowired
    public AdminController(AdminRepository adminRepo, UserRepository userRepo, AgencyRepository agencyRepo, JobRepository jobRepo, JobApplicationRepository jobApplicationRepo, PasswordEncoder passwordEncoder, BlacklistRepository blacklistRepo, MailService mailService) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.agencyRepo = agencyRepo;
        this.jobRepo = jobRepo;
        this.jobApplicationRepo = jobApplicationRepo;
        this.passwordEncoder = passwordEncoder;
        this.blacklistRepo = blacklistRepo;
        this.mailService = mailService;
    }

    @GetMapping("/login")
    public String adminLoginPage() {
        return "admin_login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Admin admin = adminRepo.findByUsernameIgnoreCase(username);
        if (admin == null || !passwordEncoder.matches(password, admin.getPassword())) {
            model.addAttribute("error", "Invalid credentials");
            return "admin_login";
        }
        session.setAttribute("loggedInAdmin", admin);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";

        List<User> allWorkers = userRepo.findAll();
        List<Agency> allAgencies = agencyRepo.findAll();

        model.addAttribute("pendingWorkers", allWorkers.stream().filter(w -> "Pending".equals(w.getStatus())).collect(Collectors.toList()));
        model.addAttribute("approvedWorkers", allWorkers.stream().filter(w -> "Approved".equals(w.getStatus())).collect(Collectors.toList()));
        model.addAttribute("pendingAgencies", allAgencies.stream().filter(a -> "Pending".equals(a.getStatus())).collect(Collectors.toList()));
        model.addAttribute("approvedAgencies", allAgencies.stream().filter(a -> "Approved".equals(a.getStatus())).collect(Collectors.toList()));

        model.addAttribute("blacklist", blacklistRepo.findAll());

        return "admin_dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/approve/worker/{id}")
    public String approveWorker(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        userRepo.findById(id).ifPresent(user -> {
            user.setStatus("Approved");
            userRepo.save(user);
            // Notify worker via email if username is an email
            mailService.sendIfEmail(
                    user.getUsername(),
                    "Your Labour-Net worker account has been approved",
                    "Hello " + user.getName() + ",\n\nYour worker account has been approved. You can now log in and apply for jobs.\n\nThanks,\nLabour-Net Team"
            );
        });
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/approve/agency/{id}")
    public String approveAgency(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        agencyRepo.findById(id).ifPresent(agency -> {
            agency.setStatus("Approved");
            agencyRepo.save(agency);
            // Notify agency via email if username is an email
            mailService.sendIfEmail(
                    agency.getUsername(),
                    "Your Labour-Net agency account has been approved",
                    "Hello " + agency.getAgencyName() + ",\n\nYour agency account has been approved. You can now log in and post jobs.\n\nThanks,\nLabour-Net Team"
            );
        });
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/worker/{id}")
    public String deleteWorker(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        userRepo.findById(id).ifPresent(worker -> {
            // remove dependent job applications first to avoid FK constraint errors
            jobApplicationRepo.deleteByWorker(worker);
            // add to blacklist by aadhar number
            if (worker.getAadharNumber() != null && !blacklistRepo.existsByIdValue(worker.getAadharNumber())) {
                BlacklistEntry e = new BlacklistEntry();
                e.setType("WORKER");
                e.setIdValue(worker.getAadharNumber());
                e.setReason("Deleted by admin");
                blacklistRepo.save(e);
            }
            userRepo.delete(worker);
        });
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/delete/agency/{id}")
    public String deleteAgency(@PathVariable Long id, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        agencyRepo.findById(id).ifPresent(agency -> {
            // delete applications for this agency's jobs first
            List<Job> jobs = jobRepo.findByAgency(agency);
            if (jobs != null) {
                for (Job j : jobs) {
                    jobApplicationRepo.deleteByJob(j);
                }
                // then delete the jobs themselves
                for (Job j : jobs) {
                    jobRepo.delete(j);
                }
            }
            if (agency.getLicenseNumber() != null && !blacklistRepo.existsByIdValue(agency.getLicenseNumber())) {
                BlacklistEntry e = new BlacklistEntry();
                e.setType("AGENCY");
                e.setIdValue(agency.getLicenseNumber());
                e.setReason("Deleted by admin");
                blacklistRepo.save(e);
            }
            agencyRepo.delete(agency);
        });
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/view/worker/{id}")
    @Transactional(readOnly = true)
    public String viewWorkerProfile(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        userRepo.findById(id).ifPresent(worker -> {
            model.addAttribute("worker", worker);
            // DEFINITIVE FIX: Use the correct eager-fetching query
            var history = jobApplicationRepo.findByWorkerIdWithDetails(worker.getId());
            model.addAttribute("workHistory", history);

            long ratingsCount = history.stream()
                    .filter(a -> a.getWorkerRating() != null)
                    .count();
            double avgRating = history.stream()
                    .filter(a -> a.getWorkerRating() != null)
                    .mapToInt(com.mini.labour_chain.model.JobApplication::getWorkerRating)
                    .average()
                    .orElse(0.0);
            model.addAttribute("avgWorkerRating", avgRating);
            model.addAttribute("workerRatingsCount", ratingsCount);

            if (worker.getProfilePicture() != null) {
                model.addAttribute("profilePicture", Base64.getEncoder().encodeToString(worker.getProfilePicture()));
            }
        });
        return "admin_worker_profile";
    }

    @GetMapping("/view/agency/{id}")
    @Transactional(readOnly = true)
    public String viewAgencyProfile(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAdmin") == null) return "redirect:/admin/login";
        agencyRepo.findById(id).ifPresent(agency -> {
            model.addAttribute("agency", agency);
            // DEFINITIVE FIX: Use the correct eager-fetching queries
            model.addAttribute("postedJobs", jobRepo.findByAgency(agency));
            model.addAttribute("feedback", jobApplicationRepo.findByAgencyIdWithDetails(agency.getId()));
        });
        return "admin_agency_profile";
    }
}
