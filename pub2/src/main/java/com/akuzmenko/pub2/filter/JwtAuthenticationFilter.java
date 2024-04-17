package com.akuzmenko.pub2.filter;

import com.akuzmenko.pub2.security.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class JwtAuthenticationFilter extends BasicAuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER = "Bearer ";

    private final JwtService jwtService;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager,
                                   AuthenticationEntryPoint authEntryPoint,
                                   JwtService jwtService) {
        super(authenticationManager, authEntryPoint);
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        var header = request.getHeader(AUTHORIZATION_HEADER);
        var requestParameterToken = request.getParameter("token");

        if (StringUtils.hasLength(header) && header.startsWith(BEARER)) {
            var token = header.substring(BEARER.length());
            if (StringUtils.hasLength(token)) {
                authenticate(request, response, token);
            }
        } else {
            if (StringUtils.hasLength(requestParameterToken)) {
                authenticate(request, response, requestParameterToken);
            }
        }
        try {
            filterChain.doFilter(request, response);
        } catch (ServletException e) {
            if (Objects.nonNull(e.getCause())) {
                log.warn("Error message: " + e.getMessage() + " Cause error message: " + e.getCause().getMessage());
            } else {
                log.warn("Error message: " + e.getMessage());
            }
        }
    }

    @Override
    protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                              Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Override
    protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                AuthenticationException failed) throws IOException {
        try {
            this.getAuthenticationEntryPoint().commence(request, response, failed);
        } catch (ServletException e) {
            log.warn("Unsuccessful authentication.", e);
        }
    }

    private void authenticate(HttpServletRequest request, HttpServletResponse response,
                              String token) throws AuthenticationException, IOException {
        try {
            var authentication = jwtService.validateTokenAndBuildAuthentication(token);
            onSuccessfulAuthentication(request, response, authentication);
        } catch (JwtException e) {
            onUnsuccessfulAuthentication(request, response, new AuthenticationServiceException(e.getMessage(), e));
        }
    }
}
