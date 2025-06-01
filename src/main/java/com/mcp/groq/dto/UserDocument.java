package com.mcp.groq.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDocument {
    private String userId;
    private String birthYear;
    private String firstName;
    private String sureName;
}
