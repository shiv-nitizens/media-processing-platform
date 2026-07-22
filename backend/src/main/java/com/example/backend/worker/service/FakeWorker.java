package com.example.backend.worker.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class FakeWorker implements Worker {

    @Override
    public TaskStatus execute(Task task){
        try{
            TimeUnit.SECONDS.sleep(10);
        }catch(InterruptedException e){
            Thread.currentThread().interrupt();
            return TaskStatus.FAILED;
        }
        return TaskStatus.SUCCESS;
    }
}

