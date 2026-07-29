package com.example.backend.subtitle;

import com.example.backend.ai.model.WhisperSegmentResponse;
import com.example.backend.ai.model.WhisperTranscriptResponse;
import org.springframework.stereotype.Service;

@Service
public class SubtitleFormatter {

    public String formatTimestamp(double totalSeconds) {
        int hours = (int) totalSeconds / 3600;
        int minutes = ((int) totalSeconds % 3600) / 60;
        int seconds = (int) totalSeconds % 60;
        int milliseconds = (int) Math.round((totalSeconds - (int) totalSeconds) * 1000);
        if (milliseconds == 1000) {
            milliseconds = 0;
            seconds++;
            if (seconds == 60) {
                seconds = 0;
                minutes++;
                if (minutes == 60) {
                    minutes = 0;
                    hours++;
                }
            }
        }
        return String.format(
                "%02d:%02d:%02d,%03d",
                hours,
                minutes,
                seconds,
                milliseconds
        );
    }
    public String generateSrt(WhisperTranscriptResponse transcript) {
        StringBuilder builder = new StringBuilder();
        int index = 1;
        for (WhisperSegmentResponse segment : transcript.getSegments()) {
            builder.append(index++);
            builder.append("\n");
            builder.append(formatTimestamp(segment.getStart()));
            builder.append(" --> ");
            builder.append(formatTimestamp(segment.getEnd()));
            builder.append("\n");
            builder.append(segment.getText());
            builder.append("\n\n");
        }
        return builder.toString();
    }
}