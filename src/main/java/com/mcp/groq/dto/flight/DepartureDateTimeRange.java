package com.mcp.groq.dto.flight;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DepartureDateTimeRange {
    @JsonProperty("date")
    private LocalDate date;
    @JsonProperty("time")
    private LocalTime time;
}
