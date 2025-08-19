package com.recruiting.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruiting.platform.dto.LoginRequest;
import com.recruiting.platform.dto.RegisterRequest;
import com.recruiting.platform.model.User;
import com.recruiting.platform.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testRegister() throws Exception {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password");
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());

        assertTrue(userRepository.findByEmail("test@example.com").isPresent());
    }

    @Test
    public void testLogin_Success() throws Exception {
        // First, create a user
        User user = new User();
        user.setEmail("test-login@example.com");
        user.setPassword(passwordEncoder.encode("password"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(com.recruiting.platform.model.Role.ROLE_CANDIDATE);
        userRepository.save(user);

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("test-login@example.com");
        loginRequest.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    public void testLogin_Failure() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized());
    }
}
