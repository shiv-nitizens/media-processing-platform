package com.example.backend.workerDispatcher.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.worker.service.Worker;
import com.example.backend.workflow.service.WorkFlowCoordinator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class WorkerDispatcher {
     ThreadPoolTaskExecutor workerExecutor;
     Worker worker;
     WorkFlowCoordinator workFlowCoordinator;

     public WorkerDispatcher(ThreadPoolTaskExecutor workerExecutor, WorkFlowCoordinator workFlowCoordinator, Worker worker) {
         this.workerExecutor = workerExecutor;
         this.workFlowCoordinator = workFlowCoordinator;
         this.worker = worker;
     }

    public void dispatch(Task task) {
        workerExecutor.submit(() -> {
            System.out.println("[Dispatcher] Executing: " + task.getType());
            TaskStatus result = worker.execute(task);
            System.out.println("[Dispatcher] Result: " + result);
            workFlowCoordinator.handleCompletion(task, result);
        });
    }
}
