package com.example.backend.artifact.repository;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.job.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ArtifactRepository extends JpaRepository<Artifact, UUID> {

    public Optional<Artifact> findByJobAndType(Job job , ArtifactType artifactType);
}
