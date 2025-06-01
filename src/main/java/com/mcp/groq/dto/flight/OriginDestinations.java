package com.mcp.groq.dto.flight;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OriginDestinations {
    @JsonProperty("id")
    private String id;
    @JsonProperty("originLocationCode")
    private String originLocationCode;
    @JsonProperty("destinationLocationCode")
    private String destinationLocationCode;
    @JsonProperty("departureDateTimeRange")
    private DepartureDateTimeRange departureDateTimeRange;
}