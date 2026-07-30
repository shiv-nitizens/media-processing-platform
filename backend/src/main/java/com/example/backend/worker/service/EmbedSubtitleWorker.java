package com.example.backend.worker.service;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.storage.FileStorageService;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@Service
public class EmbedSubtitleWorker implements Worker {
    ArtifactRepository artifactRepository;
    FileStorageService fileStorageService;
    public EmbedSubtitleWorker(ArtifactRepository artifactRepository,FileStorageService fileStorageService) {
        this.artifactRepository = artifactRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TaskType supportedType() {
        return TaskType.EMBED_SUBTITLE;
    }

    @Override
    public TaskStatus execute(Task task) {
        try {
            Optional<Artifact> videoArtifactOpt = artifactRepository.findByJobAndType(task.getJob(),ArtifactType.VIDEO);
            Optional<Artifact> subtitleArtifactOpt =artifactRepository.findByJobAndType(task.getJob(),ArtifactType.SUBTITLE);


            if (videoArtifactOpt.isEmpty() || subtitleArtifactOpt.isEmpty()) {
                System.out.println("Video or Subtitle artifact not found.");
                return TaskStatus.FAILED;
            }

            Artifact videoArtifact = videoArtifactOpt.get();
            Artifact subtitleArtifact = subtitleArtifactOpt.get();

            Path videoPath = Path.of(videoArtifact.getLocation());
            Path subtitlePath = Path.of(subtitleArtifact.getLocation());

            Path outputPath =fileStorageService.getCaptionedVideoPath(task.getJob().getId());

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i", videoPath.toString(),
                    "-vf", "subtitles=" + subtitlePath.toString(),

                    "-c:v", "h264_nvenc",

                    "-c:a", "copy",

                    outputPath.toString()
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
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode);
            }
            Artifact artifact = Artifact.builder()
                    .job(task.getJob())
                    .producedByTask(task)
                    .type(ArtifactType.CAPTIONED_VIDEO)
                    .location(outputPath.toString())
                    .createdAt(Instant.now())
                    .build();
            artifactRepository.save(artifact);
            return TaskStatus.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return TaskStatus.FAILED;
        }
    }
}