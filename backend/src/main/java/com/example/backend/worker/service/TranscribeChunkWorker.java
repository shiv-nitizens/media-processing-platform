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
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class TranscribeChunkWorker implements Worker {

    private final ArtifactRepository artifactRepository;
    private final AiServiceClient aiServiceClient;
    private final FileStorageService fileStorageService;

    public TranscribeChunkWorker(
            ArtifactRepository artifactRepository,
            AiServiceClient aiServiceClient,
            FileStorageService fileStorageService) {

        this.artifactRepository = artifactRepository;
        this.aiServiceClient = aiServiceClient;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TaskType supportedType() {
        return TaskType.TRANSCRIBE_CHUNK;
    }

    @Override
    public TaskStatus execute(Task task) {

        try {

            UUID artifactId = UUID.fromString(task.getTaskConfig());

            Optional<Artifact> chunkArtifact =
                    artifactRepository.findById(artifactId);

            if (chunkArtifact.isEmpty()) {
                System.out.println("Chunk artifact not found.");
                return TaskStatus.FAILED;
            }

            Path chunkPath = Path.of(chunkArtifact.get().getLocation());

            WhisperTranscriptResponse response =
                    aiServiceClient.transcribe(chunkPath);

            Path transcriptPath =
                    fileStorageService.saveChunkTranscript(
                            task.getJob().getId(),
                            artifactId,
                            response
                    );

            Artifact transcriptArtifact = Artifact.builder()
                    .job(task.getJob())
                    .producedByTask(task)
                    .type(ArtifactType.TRANSCRIPT_CHUNK)
                    .location(transcriptPath.toString())
                    .chunkIndex(chunkArtifact.get().getChunkIndex())
                    .createdAt(Instant.now())
                    .build();
            artifactRepository.save(transcriptArtifact);

            return TaskStatus.SUCCESS;

        } catch (Exception e) {
            e.printStackTrace();
            return TaskStatus.FAILED;
        }
    }
}