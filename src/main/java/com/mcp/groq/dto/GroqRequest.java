package com.mcp.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroqRequest {
    @JsonProperty("model")
    private String model;
    @JsonProperty("temperature")
    private Integer temperature;
    @JsonProperty("max_completion_tokens")
    private Integer maxCompletionTokens;
    @JsonProperty("top_p")
    private Integer topP;
    @JsonProperty("stream")
    private Boolean stream;
    @JsonProperty("stop")
    private String stop;
    @JsonProperty("messages")
    private List<GroqMessage> messages;
    @JsonProperty("tools")
    private List<GroqTool> tools;
    @JsonProperty("tool_choice")
    private String toolChoice;
}
