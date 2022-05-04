package com.company.security.exception;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

public class OAuth2AuthenticationProcessingException extends RuntimeException {

    public OAuth2AuthenticationProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

    public OAuth2AuthenticationProcessingException(String message) {
        super(message);
    }
}
