package com.munninlabs.oauth.security;

import com.munninlabs.oauth.entities.User;
import com.munninlabs.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class OAuth2UserService extends OidcUserService {

    private final UserRepository userRepository;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);
        Map<String, Object> attrs = oidcUser.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId(); // "google"
        String providerId = attrs.getOrDefault("sub", "").toString();
        String email = attrs.getOrDefault("email", "").toString();
        String name = attrs.getOrDefault("name", "").toString();
        String picture = attrs.getOrDefault("picture", "").toString();

        if (email != null && !email.isEmpty()) {
            userRepository.findByEmail(email).map(existing -> {
                existing.setName(name);
                existing.setProfilePicture(picture);
                existing.setProvider(provider);
                existing.setProviderId(providerId);
                return userRepository.save(existing);
            }).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .name(name)
                        .profilePicture(picture)
                        .provider(provider)
                        .providerId(providerId)
                        .build();
                return userRepository.save(newUser);
            });
        }

        return oidcUser;
    }

}
