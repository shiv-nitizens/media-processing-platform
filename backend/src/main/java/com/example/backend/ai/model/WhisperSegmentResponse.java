package com.example.backend.ai.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WhisperSegmentResponse {
    double start;
    double end;
    String text;
}
