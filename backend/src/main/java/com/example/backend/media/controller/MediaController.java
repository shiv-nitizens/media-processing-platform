package com.example.backend.media.controller;

import com.example.backend.media.dto.MediaUploadResponse;
import com.example.backend.media.service.MediaService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/media")
public class MediaController {

    private final MediaService mediaService;

    public MediaController(MediaService mediaService) {
        this.mediaService = mediaService;
    }

    @PostMapping("/upload")
    public MediaUploadResponse upload(@RequestParam MultipartFile file) {
        return mediaService.upload(file);
    }
}