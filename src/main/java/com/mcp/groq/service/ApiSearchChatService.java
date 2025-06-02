package com.mcp.groq.service;

import lombok.Getter;

@Getter
public class ApiSearchChatService implements Tool{
    private final String name = "flightSearchTool";

    @Override
    public String execute(String input) {
        return null;
    }
}
