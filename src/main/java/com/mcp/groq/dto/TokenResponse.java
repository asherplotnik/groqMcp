package com.mcp.groq.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TokenResponse {
    @JsonProperty("access_token")
    private String accessToken;
    @JsonProperty("state")
    private String state;
}
