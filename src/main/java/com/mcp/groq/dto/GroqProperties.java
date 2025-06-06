package com.mcp.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroqProperties {
    @JsonProperty("collection")
    private GroqProperty collection;
    @JsonProperty("filter")
    private GroqProperty filter;
    @JsonProperty
    private GroqProperty origin;
    @JsonProperty
    private GroqProperty destination;
    @JsonProperty
    private GroqProperty dateTime;
    @JsonProperty
    private GroqProperty travelers;
}

