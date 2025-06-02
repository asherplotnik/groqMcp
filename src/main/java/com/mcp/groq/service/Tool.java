package com.mcp.groq.service;

public interface Tool {
    String getName();
    <T> T execute(String input);
}
