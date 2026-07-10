package com.example.backend.job.controller;

import com.example.backend.job.dto.CreateJobRequest;
import com.example.backend.job.dto.JobResponse;
import com.example.backend.job.service.JobService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public JobResponse createJob(@RequestBody CreateJobRequest createJobRequest) {
        return jobService.createJob(createJobRequest);
    }
}
