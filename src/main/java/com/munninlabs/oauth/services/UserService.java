package com.munninlabs.oauth.services;

import com.munninlabs.oauth.entities.User;
import com.munninlabs.oauth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        return user.orElse(null);
    }

    public User createUserIfNotExists(String name, String email, String providerId, String profilePicture) {
        return userRepository.findByEmail(email).orElseGet(() -> {
            User newUser = new User();
            newUser.setName(name);
            newUser.setEmail(email);
            newUser.setProviderId(providerId);
            newUser.setProfilePicture(profilePicture);
            return userRepository.save(newUser);
        });
    }
}
