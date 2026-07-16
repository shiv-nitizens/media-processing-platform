package com.example.backend.worker.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FakeWorker implements Worker {

    @Override
    public TaskStatus execute(Task task){
        System.out.println("[FakeWorker] Executing task: " + task.getType());
        try{
            TimeUnit.SECONDS.sleep(2);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            return TaskStatus.FAILED;
        }
        System.out.println("[FakeWorker] Completed task: " + task.getType());
        return TaskStatus.SUCCESS;
    }
}

