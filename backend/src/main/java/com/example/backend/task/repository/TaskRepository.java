package com.example.backend.task.repository;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>{

    Optional<Task> findTopByStatusOrderByCreatedAtAsc(TaskStatus status);
}
