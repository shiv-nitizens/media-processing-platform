package com.example.backend.media.service;

import com.example.backend.media.dto.MediaUploadResponse;
import com.example.backend.media.entity.MediaFile;
import com.example.backend.media.repository.MediaFileRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class MediaService {

    MediaFileRepository mediaFileRepository;
    @Value("${media.upload.directory}")
    String uploadDirectory;

    public MediaService(MediaFileRepository mediaFileRepository){
        this.mediaFileRepository = mediaFileRepository;
    }

    //MultipartFile isn't just "a file"—it's Spring's abstraction over an uploaded file,
    // giving you metadata and access to its contents without you ever touching the raw HTTP request.
    public MediaUploadResponse upload(MultipartFile file){

        if(file.isEmpty()){
            throw new IllegalArgumentException("Uploaded file is empty.");
        }

        String originalFileName = file.getOriginalFilename();

        if(originalFileName == null || !originalFileName.contains(".")){
            throw new IllegalArgumentException("Invalid file name.");
        }

        int lastDotIndex = originalFileName.lastIndexOf(".");
        String extension = originalFileName.substring(lastDotIndex+1);

        UUID uuid = UUID.randomUUID();
        String storedFileName = "%s.%s".formatted(uuid,extension);

        Path uploadPath = Path.of(uploadDirectory);
        Path destination = uploadPath.resolve(storedFileName);

        try {
            Files.createDirectories(uploadPath);
            Files.copy(file.getInputStream(), destination);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded file.", e);
        }
        MediaFile mediaFile = MediaFile.builder()
                .originalFileName(originalFileName)
                .storedFileName(storedFileName)
                .filePath(destination.toString())
                .fileSize(file.getSize())
                .mimeType(file.getContentType())
                .build();

        mediaFile = mediaFileRepository.save(mediaFile);

        return MediaUploadResponse.builder()
                .id(mediaFile.getId())
                .originalFileName(mediaFile.getOriginalFileName())
                .uploadedAt(mediaFile.getUploadedAt())
                .build();
    }

}
