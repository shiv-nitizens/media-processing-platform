package com.example.backend.ai.client;

import com.example.backend.ai.model.WhisperTranscriptResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.nio.file.Path;

@Service
public class AiServiceClient {

    RestClient restClient;

    @Value("${groq.api.key}")
    String apiKey;

    public AiServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public WhisperTranscriptResponse transcribe(Path audioPath) {

        FileSystemResource resource = new FileSystemResource(audioPath);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", resource);
        body.add("model", "whisper-large-v3-turbo");
        body.add("response_format", "verbose_json");
        body.add("timestamp_granularities[]", "segment");

        return restClient
                .post()
                .uri("/audio/transcriptions")
                .header("Authorization","Bearer "+apiKey)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(WhisperTranscriptResponse.class);
    }
}