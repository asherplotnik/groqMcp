package com.mcp.groq.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class GroqProperty {
    @JsonProperty("type")
    private String type;
    @JsonProperty("description")
    private String description;
    @JsonProperty("enumValues")
    private List<String> enumValues;
    @JsonProperty("items")
    private Map<String, Object> items;
    @JsonProperty("minItems")
    private Integer minItems;
}
