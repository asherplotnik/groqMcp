package com.mcp.groq.controller;

import com.mcp.groq.dto.FlightRequest;
import com.mcp.groq.dto.FreeTextRequest;
import com.mcp.groq.dto.ModelResponseDto;
import com.mcp.groq.service.ApiSearchChatService;
import com.mcp.groq.service.ApiService;
import com.mcp.groq.service.MongoSearchChatService;
import com.mcp.groq.service.MongoService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api")
@RequiredArgsConstructor
public class GroqRestController {

    private final MongoSearchChatService mongoSearchChatService;
    private final ApiService apiService;
    private final ApiSearchChatService apiSearchChatService;

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
        return mongoSearchChatService.execute(request.getText());
    }

    @PostMapping("/chat/mongo/update")
    public ModelResponseDto updateMongo(
            @RequestBody FreeTextRequest request
    ) {
        return mongoSearchChatService.execute(request.getText());
    }

    @PostMapping("/flight/search")
    public String searchFlight(@RequestBody FlightRequest requestBody) {
        return apiService.searchFlightOffer(requestBody);
    }

    @PostMapping("/chat/flight/search")
    public ModelResponseDto searchFlightWithGroq(@RequestBody FreeTextRequest request) {
        return apiSearchChatService.execute(request.getText());
    }
}
