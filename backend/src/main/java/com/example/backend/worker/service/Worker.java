package com.example.backend.worker.service;

import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;

public interface Worker {
    public TaskType supportedType();
    public TaskStatus execute(Task task);
}
