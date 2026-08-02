package com.example.backend.task.model;

public enum TaskType {
    EXTRACT_AUDIO,
    SPLIT_AUDIO,
    TRANSCRIBE_CHUNK,
    MERGE_TRANSCRIPTS,
    GENERATE_SUBTITLE,
    EMBED_SUBTITLE
}