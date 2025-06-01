package com.mcp.groq.dto.flight;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
public class CabinRestriction {
    private final String cabin = "ECONOMY";
    private final String coverage = "ALL_SEGMENTS";
    private final List<String> originDestinationIds = List.of("1");
}
