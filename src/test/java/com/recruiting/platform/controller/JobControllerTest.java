package com.recruiting.platform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recruiting.platform.model.Job;
import com.recruiting.platform.model.Role;
import com.recruiting.platform.model.User;
import com.recruiting.platform.repository.JobRepository;
import com.recruiting.platform.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    private User recruiter;

    @BeforeEach
    public void setup() {
        recruiter = new User();
        recruiter.setEmail("recruiter@example.com");
        recruiter.setPassword("password");
        recruiter.setFirstName("Recruiter");
        recruiter.setLastName("User");
        recruiter.setRole(Role.ROLE_RECRUITER);
        userRepository.save(recruiter);
    }

    @Test
    public void testGetAllJobs() throws Exception {
        Job job1 = new Job();
        job1.setTitle("Software Engineer");
        job1.setDescription("Job for a software engineer");
        job1.setLocation("Remote");
        job1.setPostedDate(LocalDateTime.now());
        job1.setRecruiter(recruiter);
        jobRepository.save(job1);

        Job job2 = new Job();
        job2.setTitle("Data Scientist");
        job2.setDescription("Job for a data scientist");
        job2.setLocation("New York");
        job2.setPostedDate(LocalDateTime.now());
        job2.setRecruiter(recruiter);
        jobRepository.save(job2);

        mockMvc.perform(get("/api/jobs"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void testGetJobById_Success() throws Exception {
        Job job = new Job();
        job.setTitle("Software Engineer");
        job.setDescription("Job for a software engineer");
        job.setLocation("Remote");
        job.setPostedDate(LocalDateTime.now());
        job.setRecruiter(recruiter);
        job = jobRepository.save(job);

        mockMvc.perform(get("/api/jobs/" + job.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", is("Software Engineer")))
                .andExpect(jsonPath("$.location", is("Remote")));
    }

    @Test
    public void testGetJobById_NotFound() throws Exception {
        mockMvc.perform(get("/api/jobs/999"))
                .andExpect(status().isNotFound());
    }
}
