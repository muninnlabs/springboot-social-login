package com.munninlabs.oauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
public class LoginController {
    // Public Home Page - will show a link to /oauth2/authorization/google
    @GetMapping("/")
    public String home() {
        return "index"; // Corresponds to index.html (optional Thymeleaf template)
    }

    // Secured page - only accessible after successful login
    @GetMapping("/secured")
    public String secured(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        // Access user attributes from the OAuth2User object
        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");

        model.addAttribute("name", name);
        model.addAttribute("email", email);
        return "secured"; // Corresponds to secured.html (optional Thymeleaf template)
    }

}
