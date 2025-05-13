package com.example.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.Collection;

@Controller
public class RedirectController {

    @GetMapping("/redirect-by-role")
    public void redirectByRole(Authentication authentication,
                               HttpServletResponse response) throws IOException {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:8081");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        if (authentication == null || !authentication.isAuthenticated()) {
            response.sendRedirect("http://localhost:8081/login.html");
            return;
        }

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (authorities.stream().anyMatch(a -> a.getAuthority().equals("ADMIN"))) {
            response.sendRedirect("http://localhost:8081/admin.html");
        }
        else if (authorities.stream().anyMatch(a -> a.getAuthority().equals("USER"))) {
            response.sendRedirect("http://localhost:8081/index.html");
        }
        else {
            response.sendRedirect("http://localhost:8081/login.html");
        }
    }
}