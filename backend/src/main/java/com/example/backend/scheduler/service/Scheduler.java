package com.example.backend.scheduler.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.repository.TaskRepository;
import com.example.backend.worker.service.Worker;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class Scheduler {

    TaskRepository taskRepository;
    Worker worker;

    public Scheduler(TaskRepository taskRepository , Worker worker){
        this.worker = worker;
        this.taskRepository = taskRepository;
    }

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
        System.out.println(result);

    }

}
