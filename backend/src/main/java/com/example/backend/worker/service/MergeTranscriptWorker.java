package com.example.backend.worker.service;

import com.example.backend.ai.model.WhisperSegmentResponse;
import com.example.backend.ai.model.WhisperTranscriptResponse;
import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.storage.FileStorageService;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class MergeTranscriptWorker implements Worker {

    ArtifactRepository artifactRepository;
    FileStorageService fileStorageService;

    public MergeTranscriptWorker(ArtifactRepository artifactRepository, FileStorageService fileStorageService) {
        this.artifactRepository = artifactRepository;
        this.fileStorageService = fileStorageService;
    }

    @Override
    public TaskType supportedType() {
        return TaskType.MERGE_TRANSCRIPTS;
    }

    @Override
    public TaskStatus execute(Task task) {
        try {
            List<Artifact> transcriptArtifacts =
                    artifactRepository.findAllByJobAndTypeOrderByChunkIndexAsc(
                            task.getJob(),
                            ArtifactType.TRANSCRIPT_CHUNK
                    );
            WhisperTranscriptResponse merged = new WhisperTranscriptResponse();
            merged.setLanguage("en");
            merged.setText("");
            List<WhisperSegmentResponse> mergedSegments = new ArrayList<>();
            double offset = 0;
            StringBuilder fullText = new StringBuilder();
            for (Artifact artifact : transcriptArtifacts) {
                WhisperTranscriptResponse response =
                        fileStorageService.readTranscript(
                                java.nio.file.Path.of(artifact.getLocation())
                        );
                fullText.append(response.getText()).append(" ");
                for (WhisperSegmentResponse segment : response.getSegments()) {
                    WhisperSegmentResponse shifted = new WhisperSegmentResponse();
                    shifted.setStart(segment.getStart() + offset);
                    shifted.setEnd(segment.getEnd() + offset);
                    shifted.setText(segment.getText());
                    mergedSegments.add(shifted);
                }
                offset += 300;
            }
            merged.setText(fullText.toString().trim());
            merged.setSegments(mergedSegments);
            java.nio.file.Path mergedTranscript =
                    fileStorageService.saveTranscript(
                            task.getJob().getId(),
                            merged
                    );
            Artifact artifact = Artifact.builder()
                    .job(task.getJob())
                    .producedByTask(task)
                    .type(ArtifactType.TRANSCRIPT)
                    .location(mergedTranscript.toString())
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