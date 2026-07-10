package com.example.backend.job.repository;

import com.example.backend.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface JobRepository extends JpaRepository<Job, UUID>{
}
