package com.campus.campuscommunity.config.ocr;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
public class GoogleVisionOcrService {

    @Value("${google.cloud.api-key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String extractText(MultipartFile imageFile) throws IOException {
        // 이미지를 Base64로 인코딩
        byte[] imageBytes = imageFile.getBytes();
        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

        // API 요청 URL
        String visionApiUrl = "https://vision.googleapis.com/v1/images:annotate?key=" + apiKey;

        // 요청 헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 요청 본문 구성
        String requestJson = "{\n" +
                "  \"requests\": [\n" +
                "    {\n" +
                "      \"image\": {\n" +
                "        \"content\": \"" + base64Image + "\"\n" +
                "      },\n" +
                "      \"features\": [\n" +
                "        {\n" +
                "          \"type\": \"TEXT_DETECTION\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // API 호출
        HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(visionApiUrl, requestEntity, String.class);

        // 응답 처리
        return parseOcrResponse(response.getBody());
    }

    private String parseOcrResponse(String responseJson) throws IOException {
        JsonNode rootNode = objectMapper.readTree(responseJson);

        // 텍스트 추출
        if (rootNode.has("responses") &&
                rootNode.get("responses").get(0).has("textAnnotations") &&
                rootNode.get("responses").get(0).get("textAnnotations").size() > 0) {

            return rootNode
                    .get("responses")
                    .get(0)
                    .get("textAnnotations")
                    .get(0)
                    .get("description")
                    .asText();
        }

        return "텍스트를 추출할 수 없습니다.";
    }
}