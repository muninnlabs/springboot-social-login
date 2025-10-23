package com.munninlabs.oauth.views;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/profile")
    public String viewProfile(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        // Log to confirm the controller is reached and the user data is available
        System.out.println("--- Successfully reached /profile page ---");
        System.out.println("Authenticated User Name (Principal ID): " + oauth2User.getName());
        System.out.println("Authenticated User Email: " + oauth2User.getAttribute("email"));

        // Add user data to the model to display it on the profile view (profile.html)
        model.addAttribute("name", oauth2User.getAttribute("name"));
        model.addAttribute("email", oauth2User.getAttribute("email"));
        model.addAttribute("picture", oauth2User.getAttribute("picture"));
        // Return the name of the template (profile.html)
        return "user-profile";
    }

    @GetMapping("/login")
    public String login() {
        return "custom_login";
    }

}

