package com.example.backend.scheduler.service;

import com.example.backend.ai.client.AiServiceClient;
import com.example.backend.ai.model.CapacityResponse;
import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import com.example.backend.task.repository.TaskRepository;
import com.example.backend.worker.service.Worker;
import com.example.backend.workerDispatcher.service.WorkerDispatcher;
import com.example.backend.workflow.service.WorkFlowCoordinator;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class Scheduler {

    TaskRepository taskRepository;
    JobRepository jobRepository;
    WorkerDispatcher workerDispatcher;
    AiServiceClient aiServiceClient;

    public Scheduler(TaskRepository taskRepository ,JobRepository jobRepository,WorkerDispatcher workerDispatcher,AiServiceClient aiServiceClient){
        this.taskRepository = taskRepository;
        this.jobRepository = jobRepository;
        this.workerDispatcher = workerDispatcher;
        this.aiServiceClient = aiServiceClient;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void tick(){
        while(true){
            Optional<Task> optionalTask = taskRepository.findNextReadyTaskForUpdate(TaskStatus.READY.name());
            if(optionalTask.isEmpty()){
                return;
            }
            Task task = optionalTask.get();
            if (task.getType() == TaskType.TRANSCRIBE_CHUNK) {
                CapacityResponse capacity = aiServiceClient.getCapacity();
                if (!capacity.isAvailable()) {
                    return;
                }
            }
            task.setStatus(TaskStatus.RUNNING);
            Job job = task.getJob();
            if(job.getStatus() == JobStatus.CREATED){
                job.setStatus(JobStatus.PROCESSING);
                job.setUpdatedAt(Instant.now());
                jobRepository.save(job);
            }
            taskRepository.save(task);
            workerDispatcher.dispatch(task);
        }

    }

}
