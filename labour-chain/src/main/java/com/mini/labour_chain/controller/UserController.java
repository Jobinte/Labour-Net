package com.mini.labour_chain.controller;

import com.mini.labour_chain.model.AadharVerification;
import com.mini.labour_chain.model.User;
import com.mini.labour_chain.repository.AadharVerificationRepository;
import com.mini.labour_chain.repository.BlacklistRepository;
import com.mini.labour_chain.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/workers")
public class UserController {

    private final UserRepository userRepository;
    private final AadharVerificationRepository aadharRepo;
    private final PasswordEncoder passwordEncoder;
    private final BlacklistRepository blacklistRepo;

    @Autowired
    public UserController(UserRepository userRepository, AadharVerificationRepository aadharRepo, PasswordEncoder passwordEncoder, BlacklistRepository blacklistRepo) {
        this.userRepository = userRepository;
        this.aadharRepo = aadharRepo;
        this.passwordEncoder = passwordEncoder;
        this.blacklistRepo = blacklistRepo;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("message", "");
        return "worker_register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String aadharNumber,
                               @RequestParam String role,
                               @RequestParam String emergencyContact,
                               @RequestParam("profilePicture") String profilePicture,
                               Model model) throws IOException {

        // Password validation: min 5 chars, 1 uppercase, 1 lowercase, 1 special char
        String passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{5,}$";
        if (!password.matches(passwordPattern)) {
            model.addAttribute("message", "❌ Password must be at least 5 characters with 1 uppercase, 1 lowercase, and 1 special character (@#$%^&+=!)");
            return "worker_register";
        }

        if (userRepository.findByUsername(username) != null) {
            model.addAttribute("message", "⚠️ Username already exists!");
            return "worker_register";
        }

        AadharVerification record = aadharRepo.findById(aadharNumber).orElse(null);
        if (record == null) {
            model.addAttribute("message", "❌ Invalid Aadhaar Number!");
            return "worker_register";
        }

        if (blacklistRepo.existsByIdValue(aadharNumber)) {
            model.addAttribute("message", "🚫 This Aadhaar number is blacklisted. Registration is not allowed.");
            return "worker_register";
        }

        if (userRepository.findByAadharNumber(aadharNumber) != null) {
            model.addAttribute("message", "⚠️ User with this Aadhaar number already registered!");
            return "worker_register";
        }

        if (!emergencyContact.matches("\\d{10}")) {
            model.addAttribute("message", "❌ Emergency contact must be 10 digits!");
            return "worker_register";
        }

        User user = new User();
        user.setUsername(username);
        user.setAadharNumber(record.getAadharNumber());
        user.setName(record.getName());
        user.setDob(record.getDob());
        user.setGender(record.getGender());
        user.setAddress(record.getAddress());
        user.setPhone(record.getPhoneNumber());
        user.setEmergencyContact(emergencyContact);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setStatus("Pending"); // CRITICAL FIX: Explicitly set status before saving

        String base64Image = profilePicture.split(",")[1];
        byte[] imageBytes = Base64.getDecoder().decode(base64Image);
        user.setProfilePicture(imageBytes);

        userRepository.save(user);
        model.addAttribute("message", "✅ Registration successful! Your account is pending admin approval.");
        return "worker_login";
    }

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("message", "");
        return "worker_login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String username,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {
        User user = userRepository.findByUsername(username);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            model.addAttribute("message", "❌ Invalid username or password!");
            return "worker_login";
        }

        if (!"Approved".equals(user.getStatus())) {
            model.addAttribute("message", "Your account is not yet approved. Please wait for an admin.");
            return "worker_login";
        }

        session.setAttribute("loggedInUser", user);
        return "redirect:/workers/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, jakarta.servlet.http.HttpServletResponse response) {
        if (session != null) {
            session.invalidate();
        }
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");
        return "redirect:/";
    }

    @GetMapping("/dashboard")
    public String workerDashboard(HttpSession session, Model model,
                                  @RequestParam(required = false) String message) {
        User loggedInUser = (User) session.getAttribute("loggedInUser");
        if (loggedInUser == null) {
            return "redirect:/workers/login";
        }

        if ("application_submitted".equals(message)) {
            model.addAttribute("successMessage", "✅ Job application submitted successfully!");
        }

        if (loggedInUser.getProfilePicture() != null) {
            model.addAttribute("profilePicture", Base64.getEncoder().encodeToString(loggedInUser.getProfilePicture()));
        }

        model.addAttribute("user", loggedInUser);
        return "worker_dashboard";
    }
}
