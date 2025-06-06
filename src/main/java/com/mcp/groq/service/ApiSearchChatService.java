package com.mcp.groq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.groq.configuration.GroqRestTemplate;
import com.mcp.groq.dto.*;
import com.mcp.groq.utils.JsonUtils;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Getter
public class ApiSearchChatService implements Tool {
    private final String name = "flightSearchTool";
    private final GroqRestTemplate groqRestTemplate;
    private final ObjectMapper objectMapper;
    private final ApiService apiService;
    private static final String TOOL_CALLS = "tool_calls";
    private static final String TOOL_NAME = "search_flights";
    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String modelName;


    public ApiSearchChatService(@Value("${groq.model}") String modelName, GroqRestTemplate groqRestTemplate, ObjectMapper objectMapper, ApiService apiService) {
        this.groqRestTemplate = groqRestTemplate;
        this.objectMapper = objectMapper;
        this.modelName = modelName;
        this.apiService = apiService;
    }

    @Override
    public ModelResponseDto execute(String input) {
        GroqMessage systemMessage = GroqMessage.builder().role("system").content("""
                    You can call the function `search_flights` to retrieve flight offers. To use it, respond with exactly:
                    {
                       "tool_calls": [
                         {
                           "id": "<unique_id>",
                           "type": "function",
                           "function": {
                             "name": "search_flights",
                             "arguments": "{ \\
                               \\"origin\\": \\"<IATA>\\", \\
                               \\"destination\\": \\"<IATA>\\", \\
                               \\"dateTime\\": \\"YYYY-MM-DDThh:mm:ss\\", \\
                               \\"travelers\\": [ \\
                                 { \\"travelerType\\": \\"ADULT|CHILD|INFANT\\", \\"id\\": <positive_integer> } \\
                               ] \\
                             }"
                           }
                         }
                       ]
                     } 
                    origin and destination must be IATA airport codes (e.g. "TLV", "BKK"). 
                    dateTime must use ISO_LOCAL_DATE_TIME format, e.g. "2025-06-20T07:30:00".
                    travelers is an array of objects, each with:
                            – travelerType: one of ADULT, CHILD, INFANT
                            – id: a positive integer identifying that traveler
                """).build();
        GroqMessage userMessage = GroqMessage.builder()
                .role("user")
                .content(input)
                .build();
        GroqRequest groqRequest = getGroqRequest(systemMessage, userMessage);
        JsonUtils.printJson("request: " + groqRequest);
        String initialResponse = groqRestTemplate
                .groqPost(URL, groqRequest, String.class);
        JsonUtils.printJson("response: " + initialResponse);
        try {
            JsonNode root = objectMapper.readTree(initialResponse);
            JsonNode choice = root.path("choices").get(0).path("message");
            String finishReason = root.path("choices").get(0).path("finish_reason").asText();

            if (TOOL_CALLS.equals(finishReason) && choice.has(TOOL_CALLS)) {
                return handleToolCall(choice, systemMessage, userMessage);
            } else {
                String content = choice.path("content").asText();
                return new ModelResponseDto(content);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse initial model response", e);
        }
    }

    private GroqRequest getGroqRequest(GroqMessage systemMessage, GroqMessage userMessage) {
        GroqProperty originProp = GroqProperty.builder()
                .type("string")
                .description("IATA code of origin airport (e.g. \"TLV\").")
                .build();

        GroqProperty destinationProp = GroqProperty.builder()
                .type("string")
                .description("IATA code of destination airport (e.g. \"BKK\").")
                .build();

        GroqProperty dateTimeProp = GroqProperty.builder()
                .type("string")
                .description("Departure date/time in ISO_LOCAL_DATE_TIME format (e.g. \"2025-06-20T07:30:00\").")
                .build();

        Map<String, Object> travelerItemSchema = Map.of(
                "type", "object",
                "properties", Map.of(
                        "travelerType", Map.of(
                                "type", "string",
                                "enum", List.of("ADULT", "CHILD", "INFANT"),
                                "description", "Type of traveler"
                        ),
                        "id", Map.of(
                                "type", "string",
                                "minimum", 1,
                                "description", "id of this this traveler"
                        )
                ),
                "required", List.of("travelerType", "id")
        );
        GroqProperty travelersProp = GroqProperty.builder()
                .type("array")
                .description("List of traveler objects with 'travelerType' and 'id'")
                .items(travelerItemSchema)
                .minItems(1)
                .build();

        GroqParameters groqParameters = GroqParameters.builder()
                .type("object")
                .properties(Map.of("origin", originProp, "destination", destinationProp, "dateTime", dateTimeProp, "travelers", travelersProp))
                .required(List.of("origin", "destination", "dateTime", "travelers"))
                .build();

        GroqFunction flightFunction = GroqFunction.builder()
                .name(TOOL_NAME)
                .description("Search flight offers given origin, destination, dateTime, and travelers.")
                .parameters(groqParameters)
                .build();

        GroqTool flightTool = GroqTool.builder()
                .type("function")
                .function(flightFunction)
                .build();

        return GroqRequest.builder()
                .model(modelName)
                .temperature(1)
                .stream(false)
                .stop(null)
                .messages(List.of(systemMessage, userMessage))
                .tools(List.of(flightTool))
                .toolChoice("auto")
                .build();
    }

    private ModelResponseDto handleToolCall(
            JsonNode choice,
            GroqMessage systemMessage,
            GroqMessage userMessage
    ) {

        JsonNode toolCalls = choice.path(TOOL_CALLS);
        JsonNode firstCall = toolCalls.get(0);
        String toolCallId = firstCall.path("id").asText();
        JsonNode functionNode = firstCall.path("function");
        String functionName = functionNode.path("name").asText();
        String argumentsJson = functionNode.path("arguments").asText();

        if (!TOOL_NAME.equals(functionName)) {
            throw new RuntimeException("Unexpected tool: " + functionName);
        }

        try {
            FlightRequest flightReq = objectMapper.readValue(argumentsJson, FlightRequest.class);
            String flightResponseJson = apiService.searchFlightOffer(flightReq);
            Map<String, Object> assistantToolCall = Map.of(
                    "role", "assistant",
                    TOOL_CALLS, List.of(
                            Map.of(
                                    "id", toolCallId,
                                    "type", "function",
                                    "function", Map.of(
                                            "name", functionName,
                                            "arguments", argumentsJson))));

            Map<String, Object> toolResponseMsg = Map.of(
                    "role", "tool",
                    "tool_call_id", toolCallId,
                    "name", functionName,
                    "content", flightResponseJson
            );

            List<Object> followUpMessages = List.of(
                    systemMessage,
                    userMessage,
                    assistantToolCall,
                    toolResponseMsg
            );
            Map<String, Object> followUpPayload = Map.of(
                    "model", modelName,
                    "messages", followUpMessages
            );

            String followUpResponse = groqRestTemplate
                    .groqPost(URL, followUpPayload, String.class);

            JsonNode followRoot = objectMapper.readTree(followUpResponse);
            JsonNode finalChoice = followRoot.path("choices").get(0).path("message");
            String finalContent = finalChoice.path("content").asText();
            return new ModelResponseDto(finalContent);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse or execute flight search", e);
        }
    }
}
