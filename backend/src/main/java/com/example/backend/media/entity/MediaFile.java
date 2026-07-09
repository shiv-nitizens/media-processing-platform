package com.example.backend.media.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "media_file")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MediaFile {
    @Id
    @GeneratedValue
    UUID id;

    String originalFileName;
    String storedFileName;
    String filePath;
    long fileSize;
    String mimeType;

    @CreationTimestamp
    private Instant uploadedAt;

}
