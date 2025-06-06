//package com.mcp.groq.service;
//
//import lombok.RequiredArgsConstructor;
//
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//@RequiredArgsConstructor
//public class ToolsSelectorService {
//    private final MongoSearchChatService mongoSearchChatService;
//    private final ApiSearchChatService apiSearchChatService;
//    private final Map<String, Tool> toolRegistry = Map.of(
//            "mongoSearchTool", mongoSearchChatService,
//            "flightSearchTool", apiSearchChatService);
//
//
//    public String handleRequest(String input) {
//        String current = input;
//        int maxCallsCounter = 0;
//        while (maxCallsCounter < 10) {
//            maxCallsCounter++;
//            String toolName = selectTool(current);
//            if (toolName == null || toolRegistry.get(toolName) == null) {
//                break;
//            }
//            current = toolRegistry.get(toolName)
//                    .execute(current);
//        }
//        return current;
//    }
//
//    private String selectTool(String text) {
//        String lower = text.toLowerCase();
//
//        if (lower.contains("fetch")) {
//            return "fetchTool";      // e.g. calls a DataFetcherTool bean
//        }
//        if (lower.contains("format")) {
//            return "formatTool";     // e.g. calls a FormatterTool bean
//        }
//        if (lower.contains("validate")) {
//            return "validateTool";   // e.g. calls a ValidatorTool bean
//        }
//
//        // No keyword matched → end of chain
//        return null;
//    }
//}
