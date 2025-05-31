package com.mcp.groq.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mcp.groq.configuration.RestTemplateUtil;
import com.mcp.groq.dto.*;
import com.mcp.groq.utils.JsonUtils;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class ChatService {
    private final RestTemplateUtil restTemplateUtil;
    private final MongoSearchService searchService;
    private final ObjectMapper objectMapper;
    private static final String FUNCTION_NAME = "tool_calls";
    private static final String URL = "https://api.groq.com/openai/v1/chat/completions";
    private final String modelName;

    public ChatService(RestTemplateUtil restTemplateUtil, MongoSearchService searchService, ObjectMapper objectMapper, @Value("${groq.model}") String modelName) {
        this.restTemplateUtil = restTemplateUtil;
        this.searchService = searchService;
        this.objectMapper = objectMapper;
        this.modelName = modelName;
    }

    public ModelResponseDto processFreeText(String userText) {
        GroqMessage systemMessage = GroqMessage.builder().role("system").content("""
                You can call find_document({collection, filter}) to read from MongoDB.
                When you do, the 'collection' must be "users" and the 'filter' must look like {"userId":"026662437"}, 
                i.e. the 'userId' field must be digits only (no "id" prefix). Do not prepend "id" to the numeric string.
                """).build();
        GroqMessage userMessage = GroqMessage.builder().role("user").content(userText).build();
        GroqRequest groqRequest = getGroqRequest(systemMessage, userMessage);
        String initialResponse = restTemplateUtil
                .post(URL, groqRequest,String.class);
        try {
            JsonNode root = objectMapper.readTree(initialResponse);
            JsonNode choice = root.path("choices").get(0).path("message");
            if (choice.has(FUNCTION_NAME)) {
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
        GroqProperty collectionProp = GroqProperty.builder()
                .type("string")
                .description("Name of the MongoDB collection (only 'users' is supported here)")
                .enumValues(List.of("users"))
                .build();

        GroqProperty filterProp = GroqProperty.builder()
                .type("object")
                .description("A JSON object with exactly one key (field name) and a string value to match")
                .build();
        GroqParameters params = GroqParameters.builder()
                .type("object")
                .properties(
                        GroqProperties.builder()
                                .collection(collectionProp)
                                .filter(filterProp)
                                .build()
                )
                .required(List.of("collection", "filter"))
                .build();
        GroqFunction findDocFunction = GroqFunction.builder()
                .name("find_document")
                .description("Find a single document in the 'users' collection using a JSON filter.")
                .parameters(params)
                .build();
        GroqTool findDocTool = GroqTool.builder()
                .type("function")
                .function(findDocFunction)
                .build();
        return GroqRequest.builder()
                .model(modelName)
                .temperature(1)
                .stream(false)
                .stop(null)
                .messages(List.of(systemMessage, userMessage))
                .tools(List.of(findDocTool))
                .toolChoice("auto")
                .build();
    }

    private ModelResponseDto handleToolCall(JsonNode choice, GroqMessage systemMessage, GroqMessage userMessage) {
        JsonNode toolCalls = choice.path(FUNCTION_NAME);
        if (!toolCalls.isArray() || toolCalls.size() == 0) {
            throw new RuntimeException("No tool_calls found in the assistant message");
        }
        JsonNode firstCall = toolCalls.get(0);
        String toolCallId = firstCall.path("id").asText();
        JsonNode functionNode = firstCall.path("function");
        String functionName = functionNode.path("name").asText();
        String argumentsJson = functionNode.path("arguments").asText();
        if (!"find_document".equals(functionName)) {
            throw new RuntimeException("Unexpected tool: " + functionName);
        }
        try {
            List<Document> docs = getDocuments(argumentsJson);
            Map<String, Object> toolResult = Map.of("found", !docs.isEmpty(),"data", docs);
            String toolResultJson = objectMapper.writeValueAsString(toolResult);
            Map<String, Object> followUpPayload = getFollowUpPayload(systemMessage, userMessage, toolCallId, functionName, argumentsJson, toolResultJson);
            String followUpResponse = restTemplateUtil.post(URL, followUpPayload, String.class);
            return createModelResponseDto(followUpResponse);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse tool arguments or results", e);
        }
    }

    private ModelResponseDto createModelResponseDto(String followUpResponse) throws JsonProcessingException {
        JsonNode followRoot = objectMapper.readTree(followUpResponse);
        JsonNode finalChoice = followRoot.path("choices").get(0).path("message");
        String finalContent = finalChoice.path("content").asText();
        return new ModelResponseDto(finalContent);
    }

    private Map<String, Object> getFollowUpPayload(GroqMessage systemMessage, GroqMessage userMessage, String toolCallId, String functionName, String argumentsJson, String toolResultJson) {
        Map<String, Object> assistantToolCall = Map.of(
                "role", "assistant",
                FUNCTION_NAME, List.of( Map.of(
                        "id", toolCallId,
                        "type", "function",
                        "function", Map.of(
                                "name", functionName,
                                "arguments", argumentsJson
                        )
                ))
        );
        Map<String, Object> toolResponseMsg = Map.of(
                "role", "tool",
                "tool_call_id", toolCallId,
                "name", functionName,
                "content", toolResultJson
        );
        List<Object> followUpMessages = List.of(
                systemMessage,
                userMessage,
                assistantToolCall,
                toolResponseMsg
        );
        return Map.of(
                "model", modelName,
                "messages", followUpMessages
        );
    }

    private List<Document> getDocuments(String argumentsJson) throws JsonProcessingException {
        @SuppressWarnings("unchecked")
        Map<String, Object> argsMap = objectMapper.readValue(
                argumentsJson, new com.fasterxml.jackson.core.type.TypeReference<>() {}
        );
        String collectionName = (String) argsMap.get("collection");
        @SuppressWarnings("unchecked")
        Map<String, Object> filterMap = (Map<String, Object>) argsMap.get("filter");
        if (filterMap.size() != 1) {
            throw new IllegalArgumentException("Filter must contain exactly one field.");
        }
        String fieldName = filterMap.keySet().iterator().next();
        Object rawValue = filterMap.get(fieldName);
        String fieldValue = rawValue.toString();
        List<Document> docs = searchService.search(collectionName, fieldName, fieldValue);
        return docs;
    }

}
