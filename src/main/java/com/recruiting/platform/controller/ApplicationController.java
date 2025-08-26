package com.recruiting.platform.controller;

import com.recruiting.platform.model.Application;
import com.recruiting.platform.service.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private static final Logger log = LoggerFactory.getLogger(ApplicationController.class);

    private final ApplicationService applicationService;

    @Autowired
    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/apply/{jobId}")
    public ResponseEntity<String> applyForJob(@PathVariable Long jobId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("[APP] Intento de aplicar sin autenticación para jobId={}", jobId);
            return new ResponseEntity<>("User not authenticated", HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();
        applicationService.applyForJob(jobId, username);
        log.info("[APP] Aplicación creada jobId={} por {}", jobId, username);
        return ResponseEntity.ok("Application submitted successfully");
    }

    @GetMapping("/my-applications")
    public ResponseEntity<List<Application>> getMyApplications(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
        String username = authentication.getName();
        List<Application> applications = applicationService.findMyApplications(username);
        log.info("[APP] Listado de aplicaciones para {} total={}", username, applications.size());
        return ResponseEntity.ok(applications);
    }
}
