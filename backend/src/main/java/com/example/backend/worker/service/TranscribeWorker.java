package com.example.backend.worker.service;

import com.example.backend.ai.client.AiServiceClient;
import com.example.backend.ai.model.WhisperTranscriptResponse;
import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.storage.FileStorageService;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Optional;

@Service
public class TranscribeWorker implements Worker{

    ArtifactRepository artifactRepository;
    AiServiceClient aiServiceClient;
    FileStorageService fileStorageService;

    public TranscribeWorker(ArtifactRepository artifactRepository,AiServiceClient aiServiceClient , FileStorageService fileStorageService) {
        this.artifactRepository = artifactRepository;
        this.aiServiceClient = aiServiceClient;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TaskType supportedType(){
        return TaskType.TRANSCRIBE_AUDIO;
    }
    @Override
    public TaskStatus execute(Task task){

        try{
            Optional<Artifact> extractAudio = artifactRepository.findByJobAndType(task.getJob(),ArtifactType.AUDIO);

            if(extractAudio.isEmpty()){
                System.out.println("Audio artifact not found for usage by transcribe worker");
                return TaskStatus.FAILED;
            }
            Artifact audioArtifact = extractAudio.get();

            Path audioPath = Paths.get(audioArtifact.getLocation());
            WhisperTranscriptResponse response =
                    aiServiceClient.transcribe(audioPath);

            Path transcriptPath =
                    fileStorageService.saveTranscript(
                            task.getJob().getId(),
                            response
                    );

            Artifact artifact = Artifact.builder()
                    .createdAt(Instant.now())
                    .producedByTask(task)
                    .job(task.getJob())
                    .location(transcriptPath.toString())
                    .type(ArtifactType.TRANSCRIPT)
                    .build();

            artifactRepository.save(artifact);

            return TaskStatus.SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
            return TaskStatus.FAILED;
        }
    }
}
