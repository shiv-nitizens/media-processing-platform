package com.example.backend.scheduler.controller;

import com.example.backend.scheduler.service.Scheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    private final Scheduler scheduler;

    public SchedulerController(Scheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/tick")
    public void tick() {
        scheduler.tick();
    }
}