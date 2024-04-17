package com.akuzmenko.pub2.security;

import com.akuzmenko.core.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException exception) throws IOException {

        var token = request.getHeader("Authorization");
        if (Objects.isNull(token)) {
            token = request.getParameter("token");
        }

        log.warn("Unauthorized error: {}. Requested URI: {}, Authorization header: {}",
                exception.getMessage(), request.getRequestURI(), token);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

        var errorResponse = ErrorResponse.builder()
                .status(HttpStatus.UNAUTHORIZED.value())
                .type(HttpStatus.UNAUTHORIZED.name())
                .message(exception.getMessage())
                .build();

        response.getOutputStream().write(objectMapper.writeValueAsBytes(errorResponse));
        response.sendError(response.getStatus());
    }
}
