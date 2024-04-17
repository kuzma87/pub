package com.akuzmenko.pub1.security;

import com.akuzmenko.pub1.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityCustomDsl extends AbstractHttpConfigurer<SecurityCustomDsl, HttpSecurity> {

    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint unauthorizedHandler;

    public static SecurityCustomDsl customDsl(JwtService jwtService, JwtAuthenticationEntryPoint entryPoint) {
        return new SecurityCustomDsl(jwtService, entryPoint);
    }

    @Override
    public void configure(HttpSecurity http) {
        var authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilterAt(new JwtAuthenticationFilter(authenticationManager, unauthorizedHandler, jwtService),
                BasicAuthenticationFilter.class);
    }
}
