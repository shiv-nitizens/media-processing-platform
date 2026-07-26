package com.example.backend.artifact.entity;

import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.job.entity.Job;
import com.example.backend.task.entity.Task;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Artifact {

    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="produced_by_task_id",nullable = false)
    Task producedByTask;

    @Enumerated(EnumType.STRING)
    ArtifactType type;

    String location;

    Instant createdAt;
}
