package com.example.backend.job.dto;

import com.example.backend.job.model.JobStatus;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class JobResponse {
    UUID jobId;
    UUID mediaFileId;
    JobStatus status;
    Instant createdAt;
}
