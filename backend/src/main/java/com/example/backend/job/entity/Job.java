package com.example.backend.job.entity;

import com.example.backend.job.model.JobStatus;
import com.example.backend.job.model.RequestedOperation;
import com.example.backend.media.entity.MediaFile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "jobs")
@Builder
public class Job {

    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "media_file_id", nullable = false)
    MediaFile mediaFile;

    UUID userId;

    @Enumerated(EnumType.STRING)
    RequestedOperation operation;

    @Enumerated(EnumType.STRING)
    JobStatus status;

    Instant createdAt;

    Instant updatedAt;
}
