package com.mcp.groq.configuration;

import com.mcp.groq.dto.TokenResponse;
import com.mcp.groq.dto.flight.*;
import com.mcp.groq.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Component
public class ApiRestTemplate {

    private final RestTemplate restTemplate;
    private final String clientId;
    private final String clientSecret;

    public ApiRestTemplate(RestTemplate restTemplate, @Value("${api.auth.clientId}")String clientId, @Value("${api.auth.clientSecret}")String clientSecret) {
        this.restTemplate = restTemplate;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    private TokenResponse getToken() {
        Map<String, String> headersMap = Map.of("Content-Type", "application/x-www-form-urlencoded");
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "client_credentials");
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        return postForObject("https://test.api.amadeus.com/v1/security/oauth2/token", body, headersMap, TokenResponse.class);
    }

    public String postFlightOffers(String origin, String destination, LocalDateTime dateTime, List<Traveler> travelerList) {
        TokenResponse tokenResponse = getToken();
        if (tokenResponse == null || !tokenResponse.getState().equals("approved")) {
            return "Unauthorized. Token not approved";
        }
        Map<String, String> headersMap = Map.of("Authorization", "Bearer " + tokenResponse.getAccessToken());
        FlightOfferRequest requestBody = getFlightOfferRequest(origin, destination, dateTime,travelerList);
        return postForObject("https://test.api.amadeus.com/v2/shopping/flight-offers", requestBody, headersMap, String.class);
    }

    private FlightOfferRequest getFlightOfferRequest(String origin, String destination, LocalDateTime dateTime, List<Traveler> travelerList) {
        return FlightOfferRequest.builder()
                .originDestinations(List.of(OriginDestinations.builder()
                        .id("1")
                        .originLocationCode(origin)
                        .destinationLocationCode(destination)
                        .departureDateTimeRange(DepartureDateTimeRange.builder()
                                .date(dateTime.toLocalDate())
                                .time(dateTime.toLocalTime())
                                .build())
                        .build()))
                .travelers(travelerList)
                .searchCriteria(SearchCriteria.builder().build())
                .build();

    }

    private <T> T postForObject(String url, Object requestBody, Map<String, String> headersMap, Class<T> responseType) {
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

    private <T> T getForObject(String url, Map<String, String> headersMap, Class<T> responseType) {
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
