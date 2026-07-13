package com.example.backend.pipelineplanner.service;

import com.example.backend.job.entity.Job;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class PipelinePlanner {

    TaskRepository taskRepository;

    public PipelinePlanner(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void plan(Job job) {

        switch (job.getOperation()) {

            case GENERATE_SUBTITLE ->
                    createSubtitlePipeline(job);

            case GENERATE_SUMMARY ->
                    createSummaryPipeline(job);

            case GENERATE_CAPTIONED_VIDEO ->
                    createCaptionedVideoPipeline(job);
        }
    }
    void createSubtitlePipeline(Job job){
        Instant now = Instant.now();
        Task extract = Task.builder()
                .job(job)
                .type(TaskType.EXTRACT_AUDIO)
                .status(TaskStatus.READY)
                .taskConfig("{}")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Task transcribe = Task.builder()
                .job(job)
                .type(TaskType.TRANSCRIBE_AUDIO)
                .status(TaskStatus.WAITING)
                .taskConfig("{}")
                .createdAt(now)
                .updatedAt(now)
                .build();

        Task subtitle =   Task.builder()
                .job(job)
                .type(TaskType.GENERATE_SUBTITLE)
                .status(TaskStatus.WAITING)
                .taskConfig("{}")
                .createdAt(now)
                .updatedAt(now)
                .build();

        transcribe.getDependencies().add(extract);
        subtitle.getDependencies().add(transcribe);

        taskRepository.saveAll(List.of(extract, transcribe, subtitle));
    }
    void createSummaryPipeline(Job job) {

    }

    void createCaptionedVideoPipeline(Job job) {

    }
}
