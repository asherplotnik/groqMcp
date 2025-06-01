package com.mcp.groq.controller;

import com.mcp.groq.configuration.ApiRestTemplate;
import com.mcp.groq.dto.FreeTextRequest;
import com.mcp.groq.dto.ModelResponseDto;
import com.mcp.groq.dto.flight.Traveler;
import com.mcp.groq.service.MongoSearchChatService;
import com.mcp.groq.service.MongoService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController("/api")
@RequiredArgsConstructor
public class GroqRestController {

    private final MongoSearchChatService mongoSearchChatService;
    private final ApiRestTemplate apiRestTemplate;

    private final MongoService service;

    @GetMapping("/mongo/search")
    public List<Document> search(@RequestParam("collection") String collectionName, @RequestParam("field") String fieldName, @RequestParam("value") String fieldValue
    ) {
        return service.search(collectionName, fieldName, fieldValue);
    }

    @PostMapping("/chat/mongo/search")
    public ModelResponseDto searchMongo(
            @RequestBody FreeTextRequest request
    ) {
        return mongoSearchChatService.processFreeText(request.getText());
    }

    @PostMapping("/chat/mongo/update")
    public ModelResponseDto updateMongo(
            @RequestBody FreeTextRequest request
    ) {
        return mongoSearchChatService.processFreeText(request.getText());
    }

    @PostMapping("/flight/search")
    public String searchFlight(@RequestBody FlightRequest requestBody) {
        return apiRestTemplate.postFlightOffers(requestBody.getOrigin(), requestBody.getDestination(), requestBody.getDateTime(), requestBody.getTravelers());
    }
}
