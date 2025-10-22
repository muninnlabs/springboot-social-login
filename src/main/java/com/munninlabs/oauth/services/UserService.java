package com.munninlabs.oauth.services;

import com.munninlabs.oauth.entities.User;
import com.munninlabs.oauth.repositories.UserRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User saveOrUpdateUser(OAuth2User user, String provider) {
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String picture = user.getAttribute("picture");
        String providerId = user.getName(); // For Google, this is the 'sub' ID

        Optional<User> existingUser = userRepository.findByEmail(email);
        User userToSave;

        if (existingUser.isPresent()) {
            // Update existing user's details
            userToSave = existingUser.get();
            userToSave.setName(name);
            userToSave.setProfilePicture(picture);
        } else {
            // Create new user
            userToSave = User.builder()
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .providerId(providerId)
                    .profilePicture(picture)
                    .build();
        }

        return userRepository.save(userToSave);
    }
}
