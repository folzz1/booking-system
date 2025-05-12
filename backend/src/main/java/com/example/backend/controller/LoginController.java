package com.example.backend.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String showLoginPage() {
        return "redirect:http://localhost:8081/login.html";
    }

    @GetMapping("/book-room")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String showBookRoomPage() {
        return "redirect:http://localhost:8081/book-room.html";
    }

}