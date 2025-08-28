package com.recruiting.platform.service;

import com.recruiting.platform.model.Job;
import com.recruiting.platform.repository.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    @Autowired
    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public List<Job> findAll() {
        return jobRepository.findAll();
    }

    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    public Job save(Job job) {
        return jobRepository.save(job);
    }

    public Job update(Long id, Job jobDetails) {
        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new com.recruiting.platform.exception.ResourceNotFoundException("Job not found with id: " + id));

        job.setTitle(jobDetails.getTitle());
        job.setDescription(jobDetails.getDescription());
        job.setLocation(jobDetails.getLocation());
        job.setSalary(jobDetails.getSalary());
        job.setEmploymentType(jobDetails.getEmploymentType());

        return jobRepository.save(job);
    }

    public void deleteById(Long id) {
        jobRepository.deleteById(id);
    }
}
