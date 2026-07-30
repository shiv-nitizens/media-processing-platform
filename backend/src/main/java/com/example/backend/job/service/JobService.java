package com.example.backend.job.service;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.job.dto.CreateJobRequest;
import com.example.backend.job.dto.JobResponse;
import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.media.entity.MediaFile;
import com.example.backend.media.repository.MediaFileRepository;
import com.example.backend.pipelineplanner.service.PipelinePlanner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class JobService{

    JobRepository jobRepository;
    MediaFileRepository mediaFileRepository;
    PipelinePlanner pipelinePlanner;
    ArtifactRepository artifactRepository;

    public JobService(JobRepository jobRepository, MediaFileRepository mediaFileRepository,PipelinePlanner pipelinePlanner,ArtifactRepository artifactRepository){
        this.jobRepository = jobRepository;
        this.mediaFileRepository = mediaFileRepository;
        this.pipelinePlanner = pipelinePlanner;
        this.artifactRepository = artifactRepository;
    }

    @Transactional
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

        job.setOperation(createJobRequest.getOperation());

        Job savedJob = jobRepository.save(job);
        Artifact videoArtifact = Artifact.builder()
                .job(savedJob)
                .type(ArtifactType.VIDEO)
                .location(mediaFile.getFilePath())
                .createdAt(Instant.now())
                .build();
        Artifact saved = artifactRepository.save(videoArtifact);
        System.out.println("Saved VIDEO artifact id = " + saved.getId());
        pipelinePlanner.plan(savedJob);

        return new JobResponse(
                savedJob.getId(),
                savedJob.getMediaFile().getId(),
                savedJob.getStatus(),
                savedJob.getCreatedAt()
        );
    }
}
