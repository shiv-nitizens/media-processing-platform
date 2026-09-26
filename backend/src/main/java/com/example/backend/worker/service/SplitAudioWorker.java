package com.example.backend.worker.service;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.storage.FileStorageService;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import com.example.backend.task.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SplitAudioWorker implements Worker {

    ArtifactRepository artifactRepository;
    FileStorageService fileStorageService;
    TaskRepository taskRepository;

    public SplitAudioWorker(ArtifactRepository artifactRepository, FileStorageService fileStorageService, TaskRepository taskRepository) {
        this.artifactRepository = artifactRepository;
        this.fileStorageService = fileStorageService;
        this.taskRepository = taskRepository;
    }
    @Override
    public TaskType supportedType() {
        return TaskType.SPLIT_AUDIO;
    }
    @Override
    public TaskStatus execute(Task task) {
        try {
            Optional<Artifact> audioArtifact = artifactRepository.findByJobAndType(task.getJob(), ArtifactType.AUDIO);

            if (audioArtifact.isEmpty()) {
                System.out.println("Audio artifact not found.");
                return TaskStatus.FAILED;
            }

            Path audioPath = Path.of(audioArtifact.get().getLocation());
            Path chunkDirectory = fileStorageService.getChunkDirectory(task.getJob().getId());
            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", audioPath.toString(),
                    "-f", "segment",
                    "-segment_time", "300",
                    "-ac", "1",
                    "-ar", "16000",
                    "-c:a", "pcm_s16le",
                    chunkDirectory.resolve("chunk_%03d.wav").toString()
            );
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader = new BufferedReader(
                                 new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[FFmpeg] " + line);
                }
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                return TaskStatus.FAILED;
            }
            List<Task> transcriptionTasks = new ArrayList<>();
            List<Path> chunks = Files.list(chunkDirectory)
                    .filter(Files::isRegularFile)
                    .sorted()
                    .toList();
            for (int i = 0; i < chunks.size(); i++) {
                Path chunk = chunks.get(i);
                Artifact artifact = Artifact.builder()
                        .job(task.getJob())
                        .producedByTask(task)
                        .type(ArtifactType.AUDIO_CHUNK)
                        .location(chunk.toString())
                        .chunkIndex(i)
                        .createdAt(Instant.now())
                        .build();
                artifact = artifactRepository.save(artifact);
                Task transcribeTask = Task.builder()
                        .job(task.getJob())
                        .type(TaskType.TRANSCRIBE_CHUNK)
                        .status(TaskStatus.READY)
                        .taskConfig(artifact.getId().toString())
                        .retryCount(0)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .build();
                taskRepository.save(transcribeTask);
                transcriptionTasks.add(transcribeTask);
            }
            Task mergeTask = Task.builder()
                    .job(task.getJob())
                    .type(TaskType.MERGE_TRANSCRIPTS)
                    .status(TaskStatus.WAITING)
                    .taskConfig("{}")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            mergeTask.getDependencies().addAll(transcriptionTasks);
            mergeTask = taskRepository.save(mergeTask);
            Task subtitleTask = Task.builder()
                    .job(task.getJob())
                    .type(TaskType.GENERATE_SUBTITLE)
                    .status(TaskStatus.WAITING)
                    .taskConfig("{}")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            subtitleTask.getDependencies().add(mergeTask);
            subtitleTask = taskRepository.save(subtitleTask);
            Task embedTask = Task.builder()
                    .job(task.getJob())
                    .type(TaskType.EMBED_SUBTITLE)
                    .status(TaskStatus.WAITING)
                    .taskConfig("{}")
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();
            embedTask.getDependencies().add(subtitleTask);
            taskRepository.save(embedTask);
            return TaskStatus.SUCCESS;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return TaskStatus.FAILED;
        } catch (Exception e) {
            e.printStackTrace();
            return TaskStatus.FAILED;
        }
    }
}