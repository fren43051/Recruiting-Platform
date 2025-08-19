package com.recruiting.platform.controller;

import com.recruiting.platform.model.Application;
import com.recruiting.platform.model.Job;
import com.recruiting.platform.model.Role;
import com.recruiting.platform.model.User;
import com.recruiting.platform.repository.ApplicationRepository;
import com.recruiting.platform.repository.JobRepository;
import com.recruiting.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ApplicationRepository applicationRepository;

    private User recruiter;
    private User candidate;
    private Job job;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();

        recruiter = new User();
        recruiter.setEmail("recruiter@example.com");
        recruiter.setPassword("password");
        recruiter.setFirstName("Recruiter");
        recruiter.setLastName("User");
        recruiter.setRole(Role.ROLE_RECRUITER);
        userRepository.save(recruiter);

        candidate = new User();
        candidate.setEmail("candidate@example.com");
        candidate.setPassword("password");
        candidate.setFirstName("Candidate");
        candidate.setLastName("User");
        candidate.setRole(Role.ROLE_CANDIDATE);
        userRepository.save(candidate);

        job = new Job();
        job.setTitle("Software Engineer");
        job.setDescription("Job for a software engineer");
        job.setLocation("Remote");
        job.setPostedDate(LocalDateTime.now());
        job.setRecruiter(recruiter);
        jobRepository.save(job);
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = {"CANDIDATE"})
    public void testApplyForJob_Success() throws Exception {
        mockMvc.perform(post("/api/applications/apply/" + job.getId()))
                .andExpect(status().isOk());

        assertEquals(1, applicationRepository.findAll().size());
    }

    @Test
    public void testApplyForJob_Unauthenticated() throws Exception {
        mockMvc.perform(post("/api/applications/apply/" + job.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = {"CANDIDATE"})
    public void testGetMyApplications() throws Exception {
        Application application = new Application();
        application.setCandidate(candidate);
        application.setJob(job);
        application.setAppliedDate(LocalDateTime.now());
        application.setStatus(com.recruiting.platform.model.ApplicationStatus.APPLIED);
        applicationRepository.save(application);

        mockMvc.perform(get("/api/applications/my-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].job.title", is("Software Engineer")));
    }

    @Test
    @WithMockUser(username = "another-candidate@example.com", roles = {"CANDIDATE"})
    public void testGetMyApplications_None() throws Exception {
         User anotherCandidate = new User();
        anotherCandidate.setEmail("another-candidate@example.com");
        anotherCandidate.setPassword("password");
        anotherCandidate.setFirstName("Another");
        anotherCandidate.setLastName("Candidate");
        anotherCandidate.setRole(Role.ROLE_CANDIDATE);
        userRepository.save(anotherCandidate);

        mockMvc.perform(get("/api/applications/my-applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "candidate@example.com", roles = {"CANDIDATE"})
    public void testApplyForJob_Duplicate() throws Exception {
        // First application should succeed
        mockMvc.perform(post("/api/applications/apply/" + job.getId()))
                .andExpect(status().isOk());

        // Second application should fail with a 409 Conflict
        mockMvc.perform(post("/api/applications/apply/" + job.getId()))
                .andExpect(status().isConflict());
    }
}
