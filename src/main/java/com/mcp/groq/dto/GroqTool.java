package com.mcp.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroqTool {
    @JsonProperty("type")
    private String type;
    @JsonProperty("function")
    private GroqFunction function;
}
