package com.example.backend.worker.service;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;

@Service
public class ExtractAudioWorker implements Worker{

    ArtifactRepository artifactRepository;

    public ExtractAudioWorker(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public TaskType supportedType(){
        return TaskType.EXTRACT_AUDIO;
    }
    @Override
    public TaskStatus execute(Task task){

        try{
            Path inputVideo = Paths.get(
                    task.getJob()
                            .getMediaFile()
                            .getFilePath()
            );

            Path jobFolder = Paths.get("storage","jobs",task.getJob().getId().toString());
            Files.createDirectories(jobFolder);
            Path audioPath = jobFolder.resolve("audio.wav");

            ProcessBuilder processBuilder = new ProcessBuilder(
                    "ffmpeg",
                    "-y",
                    "-i",
                    inputVideo.toString(),
                    "-vn",
                    audioPath.toString()
            );

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            try (BufferedReader reader =
                         new BufferedReader(
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
            Artifact artifact = Artifact.builder()
                    .createdAt(Instant.now())
                    .producedByTask(task)
                    .job(task.getJob())
                    .location(audioPath.toString())
                    .type(ArtifactType.AUDIO)
                    .build();

            artifactRepository.save(artifact);

            return TaskStatus.SUCCESS;
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return TaskStatus.FAILED;
        }catch(Exception e){
            e.printStackTrace();
            return TaskStatus.FAILED;
        }


    }
}
