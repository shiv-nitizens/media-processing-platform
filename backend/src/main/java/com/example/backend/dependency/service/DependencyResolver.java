package com.example.backend.dependency.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class DependencyResolver {
    TaskRepository taskRepository;

    public DependencyResolver(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @Transactional
    public void unlockTasks(Task completedTask){
        List<Task> dependentTasks = taskRepository.findTasksDependingOn(completedTask);
        for (Task dependentTask : dependentTasks) {
            boolean ready =
                    dependentTask.getDependencies()
                            .stream()
                            .allMatch(dep ->
                                    dep.getStatus() == TaskStatus.SUCCESS);

            if (ready && dependentTask.getStatus() == TaskStatus.WAITING) {
                dependentTask.setStatus(TaskStatus.READY);
                dependentTask.setUpdatedAt(Instant.now());
                taskRepository.save(dependentTask);
            }
        }
    }
}
