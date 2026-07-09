package com.example.backend.media.repository;

import com.example.backend.media.entity.MediaFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MediaFileRepository extends JpaRepository<MediaFile,UUID>{

}
