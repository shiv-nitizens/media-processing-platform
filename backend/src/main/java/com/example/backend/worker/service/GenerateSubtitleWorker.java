package com.example.backend.worker.service;

import com.example.backend.ai.model.WhisperTranscriptResponse;
import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.storage.FileStorageService;
import com.example.backend.subtitle.SubtitleFormatter;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Optional;

@Service
public class GenerateSubtitleWorker implements Worker{

    FileStorageService fileStorageService;
    ArtifactRepository artifactRepository;
    SubtitleFormatter subtitleFormatter;

    public GenerateSubtitleWorker(ArtifactRepository artifactRepository, FileStorageService fileStorageService,SubtitleFormatter subtitleFormatter) {
        this.artifactRepository = artifactRepository;
        this.fileStorageService = fileStorageService;
        this.subtitleFormatter = subtitleFormatter;
    }

    @Override
    public TaskType supportedType(){
        return TaskType.GENERATE_SUBTITLE;
    }
    @Override
    public TaskStatus execute(Task task){

        try{
            Optional<Artifact> extractTranscript = artifactRepository.findByJobAndType(task.getJob(),ArtifactType.TRANSCRIPT);

            if(extractTranscript.isEmpty()){
                System.out.println("Transcribe artifact not found for usage by transcribe worker");
                return TaskStatus.FAILED;
            }
            Artifact transcriptArtifact = extractTranscript.get();
            Path transcriptPath = Path.of(transcriptArtifact.getLocation());
            WhisperTranscriptResponse transcript = fileStorageService.readTranscript(transcriptPath);
            String srt = subtitleFormatter.generateSrt(transcript);
            Path subtitlePath = fileStorageService.saveSubtitle(task.getJob().getId(),srt);

            Artifact artifact = Artifact.builder()
                    .createdAt(Instant.now())
                    .producedByTask(task)
                    .job(task.getJob())
                    .location(subtitlePath.toString())
                    .type(ArtifactType.SUBTITLE)
                    .build();

            artifactRepository.save(artifact);

            return TaskStatus.SUCCESS;
        }catch(Exception e){
            e.printStackTrace();
            return TaskStatus.FAILED;
        }


    }
}
