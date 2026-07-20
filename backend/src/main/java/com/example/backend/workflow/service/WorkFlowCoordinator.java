package com.example.backend.workflow.service;

import com.example.backend.dependency.service.DependencyResolver;
import com.example.backend.job.service.JobStatusManager;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class WorkFlowCoordinator {

    TaskRepository taskRepository;
    DependencyResolver dependencyResolver;
    JobStatusManager jobStatusManager;
    static final int MAX_RETRIES = 3;

    public WorkFlowCoordinator(TaskRepository taskRepository,DependencyResolver dependencyResolver,JobStatusManager jobStatusManager){
        this.taskRepository = taskRepository;
        this.dependencyResolver = dependencyResolver;
        this.jobStatusManager = jobStatusManager;
    }

    public void handleCompletion(Task task , TaskStatus result){
        switch(result){
            case SUCCESS -> {
                task.setStatus(TaskStatus.SUCCESS);
                task.setUpdatedAt(Instant.now());
                taskRepository.save(task);
                dependencyResolver.unlockTasks(task);
                jobStatusManager.updateJobStatus(task);
            }
            case FAILED -> {

                if(task.getRetryCount() < MAX_RETRIES){
                    task.setRetryCount(task.getRetryCount() + 1);
                    task.setStatus(TaskStatus.READY);
                    task.setUpdatedAt(Instant.now());

                    taskRepository.save(task);
                }else{
                    task.setStatus(TaskStatus.FAILED);
                    task.setUpdatedAt(Instant.now());
                    taskRepository.save(task);
                    jobStatusManager.markJobFailed(task);
                }

            }

        }
    }
}
