package com.mini.labour_chain.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class SessionSecurityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler) throws Exception {
        // Add cache control headers to prevent page caching
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        String requestURI = request.getRequestURI();
        HttpSession session = request.getSession(false);

        // Check if accessing protected admin pages
        if (requestURI.startsWith("/admin/dashboard") || requestURI.startsWith("/admin/delete") || 
            requestURI.startsWith("/admin/application")) {
            if (session == null || session.getAttribute("loggedInAdmin") == null) {
                response.sendRedirect("/admin/login?error=session_expired");
                return false;
            }
        }

        // Check if accessing protected worker pages
        if (requestURI.startsWith("/workers/dashboard") || requestURI.startsWith("/applications/worker")) {
            if (session == null || session.getAttribute("loggedInUser") == null) {
                response.sendRedirect("/workers/login?error=session_expired");
                return false;
            }
        }

        // Check if accessing protected agency pages
        if (requestURI.startsWith("/agencies/dashboard") || requestURI.startsWith("/jobs/post") || 
            requestURI.startsWith("/applications/agency")) {
            if (session == null || session.getAttribute("loggedInAgency") == null) {
                response.sendRedirect("/agencies/login?error=session_expired");
                return false;
            }
        }

        return true;
    }
}
