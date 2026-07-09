package com.example.backend.media.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaUploadResponse {
    UUID id;
    String originalFileName;
    Instant uploadedAt;
}
