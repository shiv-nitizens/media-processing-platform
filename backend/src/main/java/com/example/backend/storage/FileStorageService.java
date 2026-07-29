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
}
