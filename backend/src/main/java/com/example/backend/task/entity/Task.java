package com.example.backend.task.entity;

import com.example.backend.job.entity.Job;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="tasks")
public class Task {

    @Id
    @GeneratedValue
    UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id",nullable = false)
    Job job;

    @Enumerated(EnumType.STRING)
    TaskType type;

    @Enumerated(EnumType.STRING)
    TaskStatus status;

    @ManyToMany
    @JoinTable(
            name = "task_dependencies",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "dependency_id")
    )
    @Builder.Default
    List<Task> dependencies = new ArrayList<>();

    String taskConfig;

    @Builder.Default
    int retryCount = 0;

    UUID workerId;
    Instant createdAt;
    Instant updatedAt;
}
