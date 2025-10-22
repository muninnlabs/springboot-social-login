package com.munninlabs.oauth.config;

import com.munninlabs.oauth.services.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SpringConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    // Inject the custom service
    public SpringConfig(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/h2-console/**"))
                .authorizeHttpRequests(registry -> {
                    registry.requestMatchers("/", "/login", "/h2-console/**").permitAll();
                    registry.anyRequest().authenticated();
                })
                .oauth2Login(
                        oauth2login -> oauth2login
                                .loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .userService(customOAuth2UserService)
                                )
                                .successHandler((request, response, authentication) -> {
                            response.sendRedirect("/profile");
                        })
                )

                .build();
    }
}
