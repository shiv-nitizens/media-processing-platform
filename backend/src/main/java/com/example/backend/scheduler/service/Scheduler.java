package com.example.backend.scheduler.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import com.example.backend.worker.service.Worker;
import com.example.backend.workflow.service.WorkFlowCoordinator;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Scheduler {

    TaskRepository taskRepository;
    Worker worker;
    WorkFlowCoordinator workFlowCoordinator;

    public Scheduler(TaskRepository taskRepository , Worker worker,WorkFlowCoordinator workFlowCoordinator){
        this.worker = worker;
        this.taskRepository = taskRepository;
        this.workFlowCoordinator = workFlowCoordinator;
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
        taskRepository.save(task);

        TaskStatus result = worker.execute(task);
        workFlowCoordinator.handleCompletion(task,result);

    }

}
