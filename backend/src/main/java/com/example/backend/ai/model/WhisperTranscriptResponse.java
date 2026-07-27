package com.example.backend.ai.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WhisperTranscriptResponse {
    String text;
    String language;
    List<WhisperSegmentResponse> segments;

}