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
    public String profile(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {

        if (oauth2User != null) {
            // Check for the user details attributes
            model.addAttribute("name", oauth2User.getAttribute("name"));
            model.addAttribute("email", oauth2User.getAttribute("email"));
            model.addAttribute("photo", oauth2User.getAttribute("picture"));
            return "user-profile";
        }

        // Handle the case where the user is logged in, but not via OAuth2 (e.g., Form Login)
        // or if the attributes aren't present.
        model.addAttribute("error", "Could not retrieve OAuth 2.0 user details.");
        return "redirect:/"; // Redirect or show a different error page
    }

    @GetMapping("/login")
    public String login() {
        return "custom_login";
    }

}
