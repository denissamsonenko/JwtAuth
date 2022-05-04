package com.company.security.payload;

import lombok.Data;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@Data
public class ApiResponse {
    private boolean success;
    private String message;

    public ApiResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
}
