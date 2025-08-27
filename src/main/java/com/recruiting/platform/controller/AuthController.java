package com.recruiting.platform.controller;

import com.recruiting.platform.dto.LoginRequest;
import com.recruiting.platform.dto.RegisterRequest;
import com.recruiting.platform.model.User;
import com.recruiting.platform.service.AuthService;
import com.recruiting.platform.service.UserService;
import com.recruiting.platform.util.JwtUtil;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Autowired
    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Force CANDIDATE role for public registration
            registerRequest.setRole("ROLE_CANDIDATE");
            // Register the user
            authService.register(registerRequest);

            // Fetch the created user (for returning details)
            User user = authService.getUserByEmail(registerRequest.getEmail());

            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("message", "Usuario registrado con éxito. Ahora inicia sesión para obtener tu token");
            Map<String, Object> userMap = new java.util.LinkedHashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            userMap.put("role", user.getRole().name());
            response.put("user", userMap);

            // Log success without exposing sensitive data
            log.info("[AUTH] Usuario creado: id={}, email={}, role={}", user.getId(), user.getEmail(), user.getRole().name());

            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            // Log the error reason
            log.warn("[AUTH] Fallo al registrar usuario con email {}: {}", registerRequest.getEmail(), e.getMessage());
            Map<String, Object> errorResponse = new java.util.LinkedHashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/register/admin")
    public ResponseEntity<?> registerAdmin(@Valid @RequestBody RegisterRequest registerRequest, Authentication authentication) {
        try {
            boolean adminExists = userService.adminExists();
            boolean isAuthenticatedAdmin = authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(ga -> ga.getAuthority().equals("ROLE_ADMIN"));

            if (adminExists && !isAuthenticatedAdmin) {
                Map<String, Object> errorResponse = new java.util.LinkedHashMap<>();
                errorResponse.put("message", "No autorizado: solo un ADMIN puede crear otro administrador una vez existe uno");
                return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
            }

            // Force ADMIN role regardless of the incoming payload
            registerRequest.setRole("ROLE_ADMIN");

            authService.register(registerRequest);

            // Fetch the created user (for returning details)
            User user = authService.getUserByEmail(registerRequest.getEmail());

            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("message", "Administrador registrado con éxito. Ahora inicia sesión para obtener tu token");
            Map<String, Object> userMap = new java.util.LinkedHashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            userMap.put("role", user.getRole().name());
            response.put("user", userMap);

            log.info("[AUTH] Admin creado: id={}, email={}", user.getId(), user.getEmail());
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            log.warn("[AUTH] Fallo al registrar admin con email {}: {}", registerRequest.getEmail(), e.getMessage());
            Map<String, Object> errorResponse = new java.util.LinkedHashMap<>();
            errorResponse.put("message", e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Generate JWT token
            User user = authService.getUserByEmail(loginRequest.getEmail());
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());
            
            Map<String, Object> response = new java.util.LinkedHashMap<>();
            response.put("message", "Inicio de sesión exitoso");
            Map<String, Object> userMap = new java.util.LinkedHashMap<>();
            userMap.put("id", user.getId());
            userMap.put("email", user.getEmail());
            userMap.put("firstName", user.getFirstName());
            userMap.put("lastName", user.getLastName());
            userMap.put("role", user.getRole().name());
            response.put("user", userMap);
            response.put("token", token);
            
            // Log login success
            log.info("[AUTH] Login exitoso: email={}, role={}", user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // Log login failure (do not reveal password)
            log.warn("[AUTH] Login fallido para email {}: {}", loginRequest.getEmail(), e.getMessage());
            Map<String, Object> errorResponse = new java.util.LinkedHashMap<>();
            errorResponse.put("message", "Credenciales inválidas");
            return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
        }
    }
}