package com.example.backend.worker.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;

public interface Worker {
    public TaskStatus execute(Task task);
}
