package com.example.backend.ai.client;

import com.example.backend.ai.model.WhisperTranscriptResponse;
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

    public AiServiceClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public WhisperTranscriptResponse transcribe(Path audioPath){
        FileSystemResource resource = new FileSystemResource(audioPath);
        MultiValueMap<String , Object> body  = new LinkedMultiValueMap<>();
        body.add("file",resource);
        return restClient.post()
                .uri("/transcribe")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(WhisperTranscriptResponse.class);
    }
}
