package com.example.backend.worker.service;

import com.example.backend.artifact.entity.Artifact;
import com.example.backend.artifact.model.ArtifactType;
import com.example.backend.artifact.repository.ArtifactRepository;
import com.example.backend.task.entity.Task;
import com.example.backend.task.model.TaskStatus;
import com.example.backend.task.model.TaskType;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TranscribeWorker implements Worker{

    ArtifactRepository artifactRepository;

    public TranscribeWorker(ArtifactRepository artifactRepository) {
        this.artifactRepository = artifactRepository;
    }

    @Override
    public TaskType supportedType(){
        return TaskType.TRANSCRIBE_AUDIO;
    }
    @Override
    public TaskStatus execute(Task task){

        try{
            TimeUnit.SECONDS.sleep(10);
            Artifact artifact = Artifact.builder()
                    .createdAt(Instant.now())
                    .producedByTask(task)
                    .job(task.getJob())
                    .location("artifacts/" + task.getJob().getId() + "/audio.wav")
                    .type(ArtifactType.TRANSCRIPT)
                    .build();

            artifactRepository.save(artifact);

            return TaskStatus.SUCCESS;
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
            return TaskStatus.FAILED;
        }catch(Exception e){
            e.printStackTrace();
            return TaskStatus.FAILED;
        }


    }
}
