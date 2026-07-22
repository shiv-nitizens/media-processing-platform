package com.example.backend.scheduler.service;

import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
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

    public Scheduler(TaskRepository taskRepository ,JobRepository jobRepository,WorkerDispatcher workerDispatcher){
        this.taskRepository = taskRepository;
        this.jobRepository = jobRepository;
        this.workerDispatcher = workerDispatcher;
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
