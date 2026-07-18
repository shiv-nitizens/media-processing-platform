package com.example.backend.workflow.service;

import com.example.backend.dependency.service.DependencyResolver;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class WorkFlowCoordinator {

    TaskRepository taskRepository;
    DependencyResolver dependencyResolver;

    public WorkFlowCoordinator(TaskRepository taskRepository,DependencyResolver dependencyResolver){
        this.taskRepository = taskRepository;
        this.dependencyResolver = dependencyResolver;
    }

    public void handleCompletion(Task task , TaskStatus result){
        switch(result){
            case SUCCESS -> {
                task.setStatus(TaskStatus.SUCCESS);
                task.setUpdatedAt(Instant.now());
                taskRepository.save(task);
                dependencyResolver.unlockTasks(task);
            }
            case FAILED -> {
                task.setStatus(TaskStatus.FAILED);
                task.setUpdatedAt(Instant.now());
                taskRepository.save(task);
            }

        }
    }
}
