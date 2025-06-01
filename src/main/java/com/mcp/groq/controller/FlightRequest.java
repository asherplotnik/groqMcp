package com.mcp.groq.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.mcp.groq.dto.flight.Traveler;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FlightRequest {
    @JsonProperty("origin")
    private String origin;
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("dateTime")
    private LocalDateTime dateTime;
    @JsonProperty("travelers")
    private List<Traveler> travelers;
}
