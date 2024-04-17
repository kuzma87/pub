package com.akuzmenko.pub1.security;

import com.akuzmenko.core.Authentication;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.Nullable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    @Value("${jwt.issuer}")
    private String issuer;

    @Value("${jwt.secret}")
    private String secret;

    private Claims extractClaims(String token) {
        var key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Authentication validateTokenAndBuildAuthentication(String token) {
        var claims = extractClaims(token);

        if (!issuer.equals(claims.getIssuer())) {
            throw new JwtException("Invalid issuer");
        }

        var subject = claims.getSubject();
        if (!StringUtils.hasLength(subject)) {
            throw new JwtException("Required claim [sub] isn't present");
        }

        var authorities = parseAuthorities(claims);
        if (authorities == null) {
            throw new JwtException(String.format("Required claim %s isn't present", "scp"));
        }

        return new Authentication(subject, authorities);
    }

    @Nullable
    @SuppressWarnings("unchecked")
    private List<SimpleGrantedAuthority> parseAuthorities(Claims claims) {
        List<String> stringAuthorities = claims.get("scp", List.class);
        if (Objects.isNull(stringAuthorities)) {
            return null;
        }
        return stringAuthorities.stream().map(SimpleGrantedAuthority::new).toList();
    }

}
