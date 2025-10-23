package com.munninlabs.oauth.config;

import com.munninlabs.oauth.services.CustomOAuth2UserService;
import com.munninlabs.oauth.services.CustomOidcUserServiceAdapter; // Assuming this is now injected
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringConfig {

    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomOidcUserServiceAdapter customOidcUserServiceAdapter; // Inject the adapter

    // Inject the custom services
    public SpringConfig(
            CustomOAuth2UserService customOAuth2UserService,
            CustomOidcUserServiceAdapter customOidcUserServiceAdapter // Inject the adapter
    ) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customOidcUserServiceAdapter = customOidcUserServiceAdapter; // Assign the adapter
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Allow unauthenticated access to the home page, login page, and H2 Console
                        .requestMatchers("/", "/login", "/h2-console/**").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        // Explicitly set the login page
                        .loginPage("/login")
                        // ** FIX: Use defaultSuccessUrl to force redirect and ignore saved requests **
                        .defaultSuccessUrl("/profile", true)
                        .userInfoEndpoint(userInfo -> userInfo
                                // Register for generic OAuth2 providers
                                .userService(customOAuth2UserService)
                                // Register for OIDC providers (like Google)
                                .oidcUserService(customOidcUserServiceAdapter)
                        )
                )
                // Keep the logout configuration for completeness
                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                )
                // H2 console settings: ignore CSRF
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )
                .headers(headers -> headers
                        // Disable X-Frame-Options to allow H2 console iframe
                        .frameOptions(frameOptions -> frameOptions.disable())
                );

        return http.build();

    }
}
