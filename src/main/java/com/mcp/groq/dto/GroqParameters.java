package com.mcp.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroqParameters {
    @JsonProperty("type")
    private String type;
    @JsonProperty("required")
    private List<String> required;
    @JsonProperty("properties")
    private Map <String, GroqProperty> properties;
}
