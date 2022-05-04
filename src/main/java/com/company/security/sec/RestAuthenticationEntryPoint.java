package com.company.security.sec;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.filters.ExpiresFilter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author : Denis Samsonenko
 * @created : 04.05.2022
 */

@Slf4j
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {
        log.error("Responding with unauthorized error. Message - {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getLocalizedMessage());
    }
}
