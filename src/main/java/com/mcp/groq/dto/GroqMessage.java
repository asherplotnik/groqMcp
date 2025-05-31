package com.mcp.groq.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class GroqMessage {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;

}
