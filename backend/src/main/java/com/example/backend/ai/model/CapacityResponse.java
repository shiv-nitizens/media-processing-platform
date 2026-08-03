package com.example.backend.ai.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CapacityResponse {

    private int maxConcurrent;
    private int running;
    private boolean available;
}