package com.akuzmenko.core;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class Authentication extends AbstractAuthenticationToken {

    private final String userId;

    public Authentication(String userId, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.userId = userId;
        this.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return userId;
    }

    @Override
    public Object getPrincipal() {
        return userId;
    }

}
