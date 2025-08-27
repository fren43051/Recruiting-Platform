package com.recruiting.platform.service;

import com.recruiting.platform.model.*;
import com.recruiting.platform.repository.ApplicationRepository;
import com.recruiting.platform.repository.JobRepository;
import com.recruiting.platform.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final JobRepository jobRepository;

    @Autowired
    public ApplicationService(ApplicationRepository applicationRepository, UserRepository userRepository, JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.jobRepository = jobRepository;
    }

    public void applyForJob(Long jobId, String username) {
        User candidate = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByCandidateAndJob(candidate, job)) {
            throw new com.recruiting.platform.exception.DuplicateApplicationException("You have already applied for this job");
        }

        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.APPLIED);

        applicationRepository.save(application);
    }

    public List<Application> findMyApplications(String username) {
        User candidate = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return applicationRepository.findByCandidate(candidate);
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new com.recruiting.platform.exception.ResourceNotFoundException("Application not found with id: " + id));
    }

    public void deleteApplication(Long id) {
        Application application = getApplicationById(id);
        applicationRepository.delete(application);
    }
}
