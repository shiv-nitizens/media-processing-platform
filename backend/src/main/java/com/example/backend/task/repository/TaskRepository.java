package com.example.backend.task.repository;

import com.example.backend.job.entity.Job;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID>{

    @Query(value = """
    select *
    From tasks
    where status = :status
    order by created_at asc
    LIMIT 1 
    FOR update Skip Locked          
    """,nativeQuery = true)
    Optional<Task> findNextReadyTaskForUpdate(@Param("status") String status);

    @Query("""
    SELECT t
    FROM Task t
    WHERE :task MEMBER OF t.dependencies
""")
    List<Task> findTasksDependingOn(Task task);

    boolean existsByJobAndStatusNot(Job job , TaskStatus status);
}
