package com.example.backend.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

@Controller
public class RedirectController {

    @GetMapping("/redirect-by-role")
    public void redirectByRole(Authentication authentication,
                               HttpServletResponse response) throws IOException {
        if (authentication == null) {
            response.sendRedirect("/login.html");
            return;
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            response.sendRedirect("http://localhost:8081/admin.html");
        } else {
            response.sendRedirect("http://localhost:8081/index.html");
        }
    }

    @GetMapping("/current-user-roles")
    @ResponseBody
    public Collection<? extends GrantedAuthority> getCurrentUserRoles(Authentication authentication) {
        return authentication != null ? authentication.getAuthorities() : Collections.emptyList();
    }
}