package com.example.backend.job.service;

import com.example.backend.job.dto.CreateJobRequest;
import com.example.backend.job.dto.JobResponse;
import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.media.entity.MediaFile;
import com.example.backend.media.repository.MediaFileRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class JobService{
    JobRepository jobRepository;
    MediaFileRepository mediaFileRepository;

    public JobService(JobRepository jobRepository, MediaFileRepository mediaFileRepository){
        this.jobRepository = jobRepository;
        this.mediaFileRepository = mediaFileRepository;
    }

    public JobResponse createJob(CreateJobRequest createJobRequest) {

        MediaFile mediaFile = mediaFileRepository.findById(createJobRequest.getMediaFileId())
                .orElseThrow(() -> new IllegalArgumentException("Media file not found."));

        Instant now = Instant.now();
        Job job = Job.builder()
                .mediaFile(mediaFile)
                .userId(createJobRequest.getUserId())
                .status(JobStatus.CREATED)
                .createdAt(now)
                .updatedAt(now)
                .build();

        Job savedJob = jobRepository.save(job);

        return new JobResponse(
                savedJob.getId(),
                savedJob.getMediaFile().getId(),
                savedJob.getStatus(),
                savedJob.getCreatedAt()
        );
    }
}
