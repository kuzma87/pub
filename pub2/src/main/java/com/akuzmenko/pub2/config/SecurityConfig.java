package com.akuzmenko.pub2.config;

import com.akuzmenko.pub2.security.JwtAuthenticationEntryPoint;
import com.akuzmenko.pub2.security.JwtService;
import com.akuzmenko.pub2.security.SecurityCustomDsl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtService jwtService;
    private final JwtAuthenticationEntryPoint entryPoint;
    private final List<String> publicApis = List.of("/", "/public/**");

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);

        http.authorizeHttpRequests(auth -> auth.requestMatchers(
                        publicApis.stream()
                                .map(AntPathRequestMatcher::new)
                                .toArray(RequestMatcher[]::new)
                ).permitAll()
                .anyRequest()
                .authenticated());

        http.sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http.cors(c -> {
            CorsConfigurationSource source = request -> {
                var config = new CorsConfiguration();
                config.setAllowCredentials(true);
                config.setAllowedHeaders(List.of(CorsConfiguration.ALL));
                config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
                return config;
            };
            c.configurationSource(source);
        });

        http.with(SecurityCustomDsl.customDsl(jwtService, entryPoint), Customizer.withDefaults());

        return http.build();
    }
}
