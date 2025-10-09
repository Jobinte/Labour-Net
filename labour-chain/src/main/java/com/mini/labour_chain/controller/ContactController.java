package com.mini.labour_chain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class ContactController {

    @PostMapping("/contact")
    public String sendContact(@RequestParam String name,
                              @RequestParam String email,
                              @RequestParam String message,
                              Model model) {
        try {
            // For now, just log the message instead of sending email
            System.out.println("📧 Contact Form Message Received:");
            System.out.println("From: " + name + " (" + email + ")");
            System.out.println("Message: " + message);
            
            // Simulate successful sending for demo purposes
            model.addAttribute("msg", "✅ Message received successfully! (Email temporarily disabled for demo)");
            return "contact_success"; // contact_success.html
        } catch (Exception e) {
            System.err.println("❌ Contact form error: " + e.getMessage());
            model.addAttribute("msg", "❌ Failed to send message. Try again later.");
            return "contact_success";
        }
    }

}
