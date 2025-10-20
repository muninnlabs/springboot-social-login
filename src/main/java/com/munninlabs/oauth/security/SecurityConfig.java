package com.munninlabs.oauth.security;

import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final OidcUserService OAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll() // <-- NEW CRITICAL LINE
                        .requestMatchers(
                                "/",
                                "/login",
                                "/error",
                                "/css/**",
                                "/login/oauth2/code/*",
                                "/custom_login"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(
                        oauth -> oauth
                                .loginPage("/login")
                                .userInfoEndpoint(userInfo -> userInfo
                                        .oidcUserService(OAuth2UserService)
//                                        .userService(OAuth2UserService)
                )
                                .defaultSuccessUrl("/profile", true)
                );


        return http.build();
    }
}