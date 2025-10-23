package com.munninlabs.oauth.services;

import com.munninlabs.oauth.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Custom implementation of OAuth2UserService to process user data
 * after successful authentication and persist it to the database.
 */
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final DefaultOAuth2UserService delegate;
    private final UserService userService;

    public CustomOAuth2UserService(UserService userService) {
        this.userService = userService;
        this.delegate = new DefaultOAuth2UserService();
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. Fetch the user details from the provider using the default service
        OAuth2User oauth2User = delegate.loadUser(userRequest);

        // 2. Extract provider details
        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // e.g., "google"
        // 3. Persist user data using the custom service
        User persistedUser = userService.saveOrUpdateUser(oauth2User, registrationId);
        // 4. Create a new Principal (DefaultOAuth2User) that includes the persisted user's ID
        // The persisted user's ID is used as the 'name' (Principal ID) for clarity.
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // Use a default role, or fetch roles from the DB if you have them
        Set<GrantedAuthority> authorities = Collections.singleton(new SimpleGrantedAuthority("ROLE_USER"));

        // Create a new principal with the original attributes and custom authorities
        return new DefaultOAuth2User(
                authorities,
                oauth2User.getAttributes(),
                userNameAttributeName
        );
    }
}
