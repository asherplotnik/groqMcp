package com.mcp.groq.dto.flight;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlightFilters {
    @JsonProperty("cabinRestrictions")
    private List<CabinRestriction> cabinRestrictions;

}
