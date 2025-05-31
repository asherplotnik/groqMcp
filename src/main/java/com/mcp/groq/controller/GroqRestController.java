package com.mcp.groq.controller;

import com.mcp.groq.dto.FreeTextRequest;
import com.mcp.groq.dto.ModelResponseDto;
import com.mcp.groq.service.ChatService;
import com.mcp.groq.service.MongoSearchService;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/api")
@RequiredArgsConstructor
public class GroqRestController {

    private final ChatService chatService;

    private final MongoSearchService service;

    @GetMapping("/search")
    public List<Document> search(@RequestParam("collection") String collectionName, @RequestParam("field") String fieldName, @RequestParam("value") String fieldValue
    ) {
        return service.search(collectionName, fieldName, fieldValue);
    }

    @PostMapping("/chat/send")
    public ModelResponseDto sendToModel(
            @RequestBody FreeTextRequest request
    ) {
        return chatService.processFreeText(request.getText());
    }
}
