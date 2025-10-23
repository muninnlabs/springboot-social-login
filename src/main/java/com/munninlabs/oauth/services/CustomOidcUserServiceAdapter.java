package com.munninlabs.oauth.services;

import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

/**
 * Adapter service that bridges the OIDCUserService interface required by Spring Security
 * for providers like Google, to the generic CustomOAuth2UserService implementation.
 */
@Service
public class CustomOidcUserServiceAdapter extends OidcUserService {

    private final CustomOAuth2UserService customOAuth2UserService;

    public CustomOidcUserServiceAdapter(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    /**
     * This method is called by Spring Security during an OIDC login flow.
     * We delegate the core logic (saving the user) to the generic service.
     */
    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        // 1. First, delegate to the parent OidcUserService to properly fetch and parse
        //    the OIDC tokens and claims. This returns the required OidcUser type,
        //    which is the principal Spring Security needs.
        OidcUser oidcUser = super.loadUser(userRequest);

        // 2. We execute the custom logic (persistence/database save) for the side-effect.
        //    We call your existing custom service method, which the logs confirmed works
        //    to save the user data. We ignore its return value, as attempting to cast it
        //    to OidcUser caused the ClassCastException.
        customOAuth2UserService.loadUser(userRequest);

        // 3. Return the authentic OidcUser principal obtained in step 1. This ensures
        //    the OIDC authentication flow completes successfully without the type error
        //    and allows the configured redirect to /profile to occur.
        return oidcUser;
    }
}
