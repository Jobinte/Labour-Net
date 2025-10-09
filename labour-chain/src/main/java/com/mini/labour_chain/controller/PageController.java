package com.mini.labour_chain.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/workers")
    public String workerOptions() {
        return "worker-options"; // Thymeleaf page
    }

    @GetMapping("/agencies")
    public String agencyOptions() {
        return "agency-options"; // Thymeleaf page
    }

    @GetMapping("/admin")
    public String adminLogin() {
        return "redirect:/admin/login"; // Redirect to existing admin login route
    }
}
