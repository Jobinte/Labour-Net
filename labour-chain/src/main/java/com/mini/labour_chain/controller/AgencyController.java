package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.*;
import com.mini.labour_chain.repository.*;
import com.mini.labour_chain.service.MailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Base64;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/agencies")
public class AgencyController {

    private final AgencyRepository agencyRepository;
    private final AgencyVerificationRepository agencyVerificationRepo;
    private final JobRepository jobRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistRepository blacklistRepository;
    private final MailService mailService;

    @Autowired
    public AgencyController(AgencyRepository agencyRepository, AgencyVerificationRepository agencyVerificationRepo, JobRepository jobRepository, JobApplicationRepository jobApplicationRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, BlacklistRepository blacklistRepository, MailService mailService) {
        this.agencyRepository = agencyRepository;
        this.agencyVerificationRepo = agencyVerificationRepo;
        this.jobRepository = jobRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.blacklistRepository = blacklistRepository;
        this.mailService = mailService;
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("message", "");
        return "agency_register";
    }

    @PostMapping("/register")
    public String registerAgency(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String licenseNumber,
                                 @RequestParam String agencyName,
                                 @RequestParam String bio,
                                 Model model) {

        // Password validation: min 5 chars, 1 uppercase, 1 lowercase, 1 special char
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
        if (!password.matches(passwordPattern)) {
            model.addAttribute("message", "❌ Password must be at least 5 characters with 1 uppercase, 1 lowercase, and 1 special character (@#$%^&+=!)");
            return "agency_register";
        }

        if (agencyRepository.findByUsername(username) != null) {
            model.addAttribute("message", "⚠️ Username already exists!");
            return "agency_register";
        }

        // DEFINITIVE FIX: Use the correct repository to find the verification record.
        AgencyVerification record = agencyVerificationRepo.findById(licenseNumber).orElse(null);
        if (record == null) {
            model.addAttribute("message", "❌ Invalid License Number!");
            return "agency_register";
        }

        // Block registration if license is blacklisted
        if (blacklistRepository.existsByIdValue(licenseNumber)) {
            model.addAttribute("message", "🚫 This License Number is blacklisted. Registration is not allowed.");
            return "agency_register";
        }

        if (agencyRepository.findByLicenseNumber(licenseNumber) != null) {
            model.addAttribute("message", "⚠️ Agency with this license number already registered!");
            return "agency_register";
        }

        Agency agency = new Agency();
        agency.setUsername(username);
        agency.setLicenseNumber(record.getLicenseNumber());
        agency.setProprietorName(record.getProprietorName());
        agency.setContactNumber(record.getContactNumber());
        agency.setAddress(record.getAddress());
        agency.setAgencyName(agencyName);
        agency.setBio(bio);
        agency.setPassword(passwordEncoder.encode(password));

        agencyRepository.save(agency);
        model.addAttribute("message", "✅ Registration successful! Your account is pending admin approval.");
        return "agency_login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("message", "");
        return "agency_login";
    }

    @PostMapping("/login")
    public String loginAgency(@RequestParam String username,
                              @RequestParam String password,
                              HttpSession session,
                              Model model) {
        Agency agency = agencyRepository.findByUsername(username);

        if (agency == null || !passwordEncoder.matches(password, agency.getPassword())) {
            model.addAttribute("message", "❌ Invalid username or password!");
            return "agency_login";
        }

        if (!"Approved".equals(agency.getStatus())) {
            model.addAttribute("message", "Your account is not yet approved. Please wait for an admin.");
            return "agency_login";
        }

        session.setAttribute("loggedInAgency", agency);
        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    @Transactional(readOnly = true)
    public String showAgencyDashboard(HttpSession session, Model model) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        // DEFINITIVE FIX: Use the eager-fetching queries.
        List<Job> postedJobs = jobRepository.findByAgency(loggedInAgency);
        List<JobApplication> applications = postedJobs.isEmpty() ? Collections.emptyList() : jobApplicationRepository.findByJobIn(postedJobs);

        model.addAttribute("agency", loggedInAgency);
        model.addAttribute("postedJobs", postedJobs);
        model.addAttribute("applications", applications);

        return "agency_dashboard";
    }

    @PostMapping("/application/reply/{id}")
    public String replyToApplication(@PathVariable Long id, @RequestParam String reply, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        JobApplication application = jobApplicationRepository.findById(id).orElse(null);
        if (application != null && application.getJob().getAgency().equals(loggedInAgency)) {
            application.setReply(reply);
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "Reply sent successfully!");
            // Notify worker
            mailService.sendIfEmail(
                    application.getWorker().getUsername(),
                    "Update on your job application",
                    "Hello " + application.getWorker().getName() + ",\n\nAgency '" + loggedInAgency.getAgencyName() + "' sent a reply to your application for '" + application.getJob().getTitle() + "'.\n\nReply: " + reply + "\n\nThanks,\nLabour-Net Team"
            );
        }

        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/application/approve/{id}")
    public String approveApplication(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        JobApplication application = jobApplicationRepository.findById(id).orElse(null);
        if (application != null && application.getJob().getAgency().equals(loggedInAgency)) {
            application.setStatus("Approved");
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "Application approved successfully!");
            // Notify worker
            mailService.sendIfEmail(
                    application.getWorker().getUsername(),
                    "Your application was approved",
                    "Hello " + application.getWorker().getName() + ",\n\nGood news! Your application for '" + application.getJob().getTitle() + "' was approved by '" + loggedInAgency.getAgencyName() + "'.\n\nPlease check your dashboard for next steps.\n\nThanks,\nLabour-Net Team"
            );
        }

        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/application/reject/{id}")
    public String rejectApplication(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        JobApplication application = jobApplicationRepository.findById(id).orElse(null);
        if (application != null && application.getJob().getAgency().equals(loggedInAgency)) {
            application.setStatus("Rejected");
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "Application rejected successfully!");
            // Notify worker
            mailService.sendIfEmail(
                    application.getWorker().getUsername(),
                    "Your application was rejected",
                    "Hello " + application.getWorker().getName() + ",\n\nYour application for '" + application.getJob().getTitle() + "' was rejected by '" + loggedInAgency.getAgencyName() + "'.\n\nYou can apply to other jobs on Labour-Net.\n\nThanks,\nLabour-Net Team"
            );
        }

        return "redirect:/agencies/dashboard";
    }

    @GetMapping("/worker/{id}")
    @Transactional(readOnly = true)
    public String viewWorkerProfile(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("loggedInAgency") == null) {
            return "redirect:/agencies/login";
        }

        User worker = userRepository.findById(id).orElse(null);
        if (worker != null) {
            model.addAttribute("worker", worker);
            List<JobApplication> history = jobApplicationRepository.findByWorkerIdWithDetails(worker.getId());
            model.addAttribute("workHistory", history);

            long ratingsCount = history.stream()
                    .filter(a -> a.getWorkerRating() != null)
                    .count();
            double avgRating = history.stream()
                    .filter(a -> a.getWorkerRating() != null)
                    .mapToInt(JobApplication::getWorkerRating)
                    .average()
                    .orElse(0.0);
            model.addAttribute("avgWorkerRating", avgRating);
            model.addAttribute("workerRatingsCount", ratingsCount);

            if (worker.getProfilePicture() != null) {
                model.addAttribute("profilePicture", Base64.getEncoder().encodeToString(worker.getProfilePicture()));
            }
        }

        return "worker_profile";
    }

    @GetMapping("/profile/{agencyId}")
    @Transactional(readOnly = true)
    public String publicAgencyProfile(@PathVariable Long agencyId, Model model) {
        try {
            Agency agency = agencyRepository.findById(agencyId).orElse(null);
            if (agency == null) {
                // Gracefully handle missing agency
                return "redirect:/";
            }

            model.addAttribute("agency", agency);
            List<JobApplication> feedback = Collections.emptyList();
            try {
                feedback = jobApplicationRepository.findByAgencyIdWithDetails(agencyId);
            } catch (Exception ignored) {}
            model.addAttribute("feedback", feedback);

            long agencyRatingsCount = feedback.stream()
                    .filter(a -> a.getAgencyRating() != null)
                    .count();
            double avgAgencyRating = feedback.stream()
                    .filter(a -> a.getAgencyRating() != null)
                    .mapToInt(JobApplication::getAgencyRating)
                    .average()
                    .orElse(0.0);
            model.addAttribute("avgAgencyRating", avgAgencyRating);
            model.addAttribute("agencyRatingsCount", agencyRatingsCount);

            return "public_agency_profile";
        } catch (Exception ex) {
            // Fallback to default error page (keeps 500 styling)
            return "error";
        }
    }

    @PostMapping("/rate-worker/{applicationId}")
    public String rateWorker(@PathVariable Long applicationId,
                             @RequestParam Integer rating,
                             @RequestParam String feedback,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        Agency loggedInAgency = (Agency) session.getAttribute("loggedInAgency");
        if (loggedInAgency == null) {
            return "redirect:/agencies/login";
        }

        JobApplication application = jobApplicationRepository.findById(applicationId).orElse(null);
        if (application != null
                && application.getJob().getAgency().equals(loggedInAgency)
                && "Confirmed".equals(application.getConfirmationStatus())) {
            application.setWorkerRating(rating);
            application.setWorkerFeedback(feedback);
            jobApplicationRepository.save(application);
            redirectAttributes.addFlashAttribute("popupMessage", "✅ Worker rated successfully!");
        }

        return "redirect:/agencies/dashboard";
    }
}
