package com.mini.labour_chain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Public pages
                        .requestMatchers("/", "/index", "/get-started", "/contact", "/contact/**").permitAll()
                        .requestMatchers("/jobs", "/jobs/**").permitAll() // Allow job viewing

                        // Worker login/register
                        .requestMatchers("/workers", "/workers/login", "/workers/register").permitAll()
                        .requestMatchers("/workers/login/**", "/workers/register/**").permitAll()

                        // Agency login/register
                        .requestMatchers("/agencies", "/agencies/login", "/agencies/register").permitAll()
                        .requestMatchers("/agencies/login/**", "/agencies/register/**").permitAll()

                        // Admin login must be PUBLIC
                        .requestMatchers("/admin/login").permitAll()

                        // Static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/static/**").permitAll()

                        // Public API for homepage chatbot
                        .requestMatchers("/api/chat").permitAll()

                        // Allow everything else for now (session-based auth)
                        .anyRequest().permitAll()
                )
                .formLogin(form -> form.disable()) // Using custom session-based authentication
                .logout(logout -> logout.logoutSuccessUrl("/").permitAll())
                .csrf(csrf -> csrf.disable()); // Disabled for simplicity

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}


