package com.munninlabs.oauth.services;

import com.munninlabs.oauth.entities.User;
import com.munninlabs.oauth.repositories.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger; // Import the Logger
import org.slf4j.LoggerFactory; // Import the LoggerFactory

import java.util.Optional;


@Service
public class UserService {

    // Initialize logger for this class
    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Saves or updates a User entity based on OAuth2 details.
     * The @Transactional annotation ensures this method runs within a database transaction,
     * guaranteeing the save operation is committed.
     */
    @Transactional
    public User saveOrUpdateUser(OAuth2User user, String provider) {
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String picture = user.getAttribute("picture");
        String providerId = user.getName();

        log.info("--- Starting saveOrUpdateUser for provider: {} ---", provider);
        log.info("Attempting to process user with email: {}", email);

        // Early exit check in case email is not provided by the OAuth provider
        if (email == null || email.isEmpty()) {
            log.error("Error: OAuth2 User data is missing required 'email' attribute for persistence. Cannot save user.");
            return null;
        }

        Optional<User> existingUser = userRepository.findByEmail(email);
        User userToSave;

        if (existingUser.isPresent()) {
            log.info("User already exists. Updating details for: {}", email);
            // Update existing user's details
            userToSave = existingUser.get();
            userToSave.setName(name);
            userToSave.setProfilePicture(picture);
        } else {
            log.info("New user detected. Creating new entry for: {}", email);
            // Create new user
            userToSave = User.builder()
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .profilePicture(picture)
                    .build();
        }

        // Save will perform an INSERT for new users or UPDATE for existing ones
        User savedUser = userRepository.save(userToSave);
        log.info("User saved successfully. DB ID: {}", savedUser.getId());
        log.info("--- Finished saveOrUpdateUser ---");

        return savedUser;
    }
}
