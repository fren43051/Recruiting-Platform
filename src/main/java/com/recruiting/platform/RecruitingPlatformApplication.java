package com.recruiting.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.recruiting.platform.repository")
@EntityScan("com.recruiting.platform.model")
public class RecruitingPlatformApplication {

    public static void main(String[] args) {
        SpringApplication.run(RecruitingPlatformApplication.class, args);
    }

}
