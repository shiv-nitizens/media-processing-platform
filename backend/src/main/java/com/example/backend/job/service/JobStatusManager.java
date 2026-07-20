package com.example.backend.job.service;

import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobStatusManager {

    JobRepository jobRepository;
    TaskRepository taskRepository;

    public JobStatusManager(JobRepository jobRepository, TaskRepository taskRepository) {
        this.jobRepository = jobRepository;
        this.taskRepository = taskRepository;
    }

    public void updateJobStatus(Task taskCompleted){
        Job job = taskCompleted.getJob();
        boolean unfinished = taskRepository.existsByJobAndStatusNot(job,TaskStatus.SUCCESS);
        if(!unfinished){
            job.setStatus(JobStatus.COMPLETED);
            job.setUpdatedAt(Instant.now());
            jobRepository.save(job);
        }
    }
    public void markJobFailed(Task failedTask){
        Job job = failedTask.getJob();
        job.setStatus(JobStatus.FAILED);
        job.setUpdatedAt(Instant.now());
        jobRepository.save(job);
    }
}
