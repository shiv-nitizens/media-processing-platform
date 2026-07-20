package com.example.backend.scheduler.service;

import com.example.backend.job.entity.Job;
import com.example.backend.job.model.JobStatus;
import com.example.backend.job.repository.JobRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import com.example.backend.worker.service.Worker;
import com.example.backend.workflow.service.WorkFlowCoordinator;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class Scheduler {

    TaskRepository taskRepository;
    Worker worker;
    WorkFlowCoordinator workFlowCoordinator;
    JobRepository jobRepository;

    public Scheduler(TaskRepository taskRepository , Worker worker,WorkFlowCoordinator workFlowCoordinator ,JobRepository jobRepository){
        this.worker = worker;
        this.taskRepository = taskRepository;
        this.workFlowCoordinator = workFlowCoordinator;
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void tick(){
        System.out.println("Scheduler tick called");
        Optional<Task> optionalTask = taskRepository.findTopByStatusOrderByCreatedAtAsc(TaskStatus.READY);
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

        TaskStatus result = worker.execute(task);
        workFlowCoordinator.handleCompletion(task,result);

    }

}
