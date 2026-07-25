package com.example.backend.workerDispatcher.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import com.example.backend.worker.service.Worker;
import com.example.backend.workflow.service.WorkFlowCoordinator;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkerDispatcher {
     ThreadPoolTaskExecutor workerExecutor;
     Map<TaskType , Worker> workerMap;
     WorkFlowCoordinator workFlowCoordinator;

     public WorkerDispatcher(ThreadPoolTaskExecutor workerExecutor, WorkFlowCoordinator workFlowCoordinator, List<Worker> workers) {
         this.workerExecutor = workerExecutor;
         this.workFlowCoordinator = workFlowCoordinator;
         this.workerMap = new HashMap<>();
         for(Worker worker : workers){
             workerMap.put(worker.supportedType(),worker);
         }
     }

    public void dispatch(Task task) {
         Worker worker = workerMap.get(task.getType());
        if (worker == null) {
            throw new IllegalArgumentException(
                    "No worker registered for task type: " + task.getType());
        }
        workerExecutor.submit(() -> {
            System.out.println("[Dispatcher] Executing: " + task.getType());
            TaskStatus result = worker.execute(task);
            System.out.println("[Dispatcher] Result: " + result);
            workFlowCoordinator.handleCompletion(task, result);
        });
    }
}
