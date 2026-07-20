package com.example.backend.task.repository;

import com.example.backend.job.entity.Job;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>{

    Optional<Task> findTopByStatusOrderByCreatedAtAsc(TaskStatus status);

    @Query("""
    SELECT t
    FROM Task t
    WHERE :task MEMBER OF t.dependencies
""")
    List<Task> findTasksDependingOn(Task task);

    boolean existsByJobAndStatusNot(Job job , TaskStatus status);
}
