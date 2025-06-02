package com.mcp.groq.dto.flight;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Getter
public class CabinRestriction {
    private final String cabin = "ECONOMY";
    private final String coverage = "ALL_SEGMENTS";
    private final List<String> originDestinationIds = List.of("1");
}
