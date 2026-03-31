package com.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class JwtDto {

    private String accessToken;
    private String refreshToken;

    private static final String TYPE = "Bearer";
}