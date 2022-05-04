package com.company.security.payload;

import lombok.Data;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@Data
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";

    public AuthResponse(String accessToken) {
        this.accessToken = accessToken;
    }
}
