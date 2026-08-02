package com.example.backend.storage;

import com.example.backend.ai.model.WhisperTranscriptResponse;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileStorageService {
    ObjectMapper objectMapper;

    public FileStorageService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Path saveTranscript(UUID jobId, WhisperTranscriptResponse transcript) throws IOException{
        Path jobFolder =
                Paths.get("storage", "jobs", jobId.toString());

        Files.createDirectories(jobFolder);
        Path transcriptFile = jobFolder.resolve("transcript.json");
        objectMapper.writeValue(
                transcriptFile.toFile(),
                transcript
        );
        return transcriptFile;
    }
    public WhisperTranscriptResponse readTranscript(Path transcriptPath) throws IOException{
        return objectMapper.readValue(
                transcriptPath.toFile(),
                WhisperTranscriptResponse.class
        );
    }
    public Path saveSubtitle(UUID jobId , String subtitleContent) throws IOException{
        Path jobFolder = Paths.get("storage","jobs",jobId.toString());
        Files.createDirectories(jobFolder);
        Path subtitleFile = jobFolder.resolve("subtitle.srt");
        Files.writeString(subtitleFile,subtitleContent);
        return subtitleFile;
    }
    public Path getCaptionedVideoPath(UUID jobId) throws IOException {
        Path jobFolder = Paths.get("storage", "jobs", jobId.toString());
        Files.createDirectories(jobFolder);
        return jobFolder.resolve("captioned-video.mp4");
    }
    public Path getChunkDirectory(UUID jobId) throws IOException {
        Path chunkDirectory = Paths.get("storage", "jobs",jobId.toString(),"chunks");
        Files.createDirectories(chunkDirectory);
        return chunkDirectory;
    }
    public Path saveChunkTranscript(UUID jobId, UUID artifactId, WhisperTranscriptResponse transcript) throws IOException {
        Path transcriptDir = Paths.get("storage", "jobs", jobId.toString(), "chunk-transcripts");
        Files.createDirectories(transcriptDir);
        Path transcriptFile = transcriptDir.resolve(artifactId + ".json");
        objectMapper.writeValue(
                transcriptFile.toFile(),
                transcript
        );
        return transcriptFile;
    }
}
