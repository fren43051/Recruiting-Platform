package com.recruiting.platform.service;

import com.recruiting.platform.dto.RegisterRequest;
import com.recruiting.platform.model.Role;
import com.recruiting.platform.model.User;
import com.recruiting.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(RegisterRequest registerRequest) {
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("User with this email already exists");
        }

        User user = new User();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setFirstName(registerRequest.getFirstName());
        user.setLastName(registerRequest.getLastName());

        // Resolve role from request; default to CANDIDATE on null/blank
        user.setRole(resolveRole(registerRequest.getRole()));

        userRepository.save(user);
    }
    
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    private Role resolveRole(String requestedRole) {
        if (requestedRole == null || requestedRole.trim().isEmpty()) {
            return Role.ROLE_CANDIDATE;
        }
        String norm = requestedRole.trim().toUpperCase();
        // Allow both with and without ROLE_ prefix
        if (!norm.startsWith("ROLE_")) {
            norm = "ROLE_" + norm;
        }
        // Alias mapping: treat USER as CANDIDATE to match common expectations
        if ("ROLE_USER".equals(norm)) {
            return Role.ROLE_CANDIDATE;
        }
        try {
            return Role.valueOf(norm);
        } catch (IllegalArgumentException ex) {
            throw new RuntimeException("Invalid role: " + requestedRole + ". Allowed: ADMIN, RECRUITER, CANDIDATE (alias: USER -> CANDIDATE)");
        }
    }
}