package com.mcp.groq.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class RestTemplateUtil {

    private final RestTemplate restTemplate;
    private final String key;

    public RestTemplateUtil(RestTemplate restTemplate, @Value("${groq.auth.key}")String key) {
        this.restTemplate = restTemplate;
        this.key = key;
    }

    public <T> T post(String url, Object requestBody, Class<T> responseType) {
        Map<String, String> headersMap = Map.of("Authorization", "Bearer " + key);
        return postForObject(url, requestBody, headersMap, responseType);
    }

    public <T> T postForObject(String url, Object requestBody, Map<String, String> headersMap, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (headersMap != null) {
            headersMap.forEach(headers::set);
        }
        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                entity,
                responseType
        );
        return responseEntity.getBody();
    }

    public <T> T get(String url, Class<T> responseType) {
        Map<String, String> headersMap = Map.of("Authorization", "Bearer " + key);
        return getForObject(url, headersMap, responseType);
    }

    public <T> T getForObject(String url, Map<String, String> headersMap, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        if (headersMap != null) {
            headersMap.forEach(headers::set);
        }
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        ResponseEntity<T> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                responseType
        );
        return responseEntity.getBody();
    }
}
