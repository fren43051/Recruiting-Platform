package com.recruiting.platform.repository;

import com.recruiting.platform.model.Application;
import com.recruiting.platform.model.Job;
import com.recruiting.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByCandidate(User candidate);
    boolean existsByCandidateAndJob(User candidate, Job job);
}
