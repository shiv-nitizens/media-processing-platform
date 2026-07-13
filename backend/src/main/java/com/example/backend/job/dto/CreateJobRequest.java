package com.example.backend.job.dto;

import com.example.backend.job.model.RequestedOperation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateJobRequest {
    UUID userId;
    UUID mediaFileId;
    RequestedOperation operation;
}
