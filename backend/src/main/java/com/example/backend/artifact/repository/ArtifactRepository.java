package com.example.backend.artifact.repository;

import com.example.backend.artifact.entity.Artifact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtifactRepository extends JpaRepository<Artifact, UUID> {
}
