package com.mcp.groq.service;

import com.mcp.groq.configuration.ApiRestTemplate;
import com.mcp.groq.dto.FlightRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApiService
{
    private final ApiRestTemplate apiRestTemplate;

    public String searchFlightOffer(FlightRequest requestBody) {
        return apiRestTemplate.postFlightOffers(requestBody.getOrigin(), requestBody.getDestination(), requestBody.getDateTime(), requestBody.getTravelers());
    }
}
