package com.munninlabs.oauth.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class LoginController {

    @RequestMapping("/")
    public String home() {
        return "Welcome!";
    }

    @RequestMapping("/user")
    public Principal user(Principal user) {
        return user;
    }

    @RequestMapping("/user-details")
    public String getUserDetails(@AuthenticationPrincipal OAuth2User oauth2User) {
        if (oauth2User != null) {
            // Example of accessing attributes from an OAuth2 user
            return "Hello, " + oauth2User.getAttribute("name") + "! Your email is: " + oauth2User.getAttribute("email");
        }
        return "User is authenticated, but not via OAuth2 (or details are unavailable).";
    }

}
