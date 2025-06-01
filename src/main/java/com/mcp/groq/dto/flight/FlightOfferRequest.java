package com.mcp.groq.dto.flight;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightOfferRequest {
    @JsonProperty("currencyCode")
    private final String currencyCode = "USD";
    @JsonProperty("originDestinations")
    private List<OriginDestinations> originDestinations;
    @JsonProperty("travelers")
    private List<Traveler> travelers;
    @JsonProperty("sources")
    private final List<String> sources = List.of("GDS");
    @JsonProperty("searchCriteria")
    private SearchCriteria searchCriteria;
}
