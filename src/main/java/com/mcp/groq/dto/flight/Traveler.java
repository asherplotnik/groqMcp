package com.mcp.groq.dto.flight;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Traveler {
    @JsonProperty("id")
    String id;
    @JsonProperty("travelerType")
    String travelerType;
}
