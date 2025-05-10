package com.example.backend.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
import java.util.Collections;

@Controller
public class RedirectController {

    @GetMapping("/redirect-by-role")
    public String redirectByRole(Authentication authentication) {
        if (authentication == null) {
            return "redirect:/login.html";
        }

        System.out.println("User roles: " + authentication.getAuthorities());

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));

        if (isAdmin) {
            return "redirect:/admin";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/current-user-roles")
    @ResponseBody
    public Collection<? extends GrantedAuthority> getCurrentUserRoles(Authentication authentication) {
        return authentication != null ? authentication.getAuthorities() : Collections.emptyList();
    }
}