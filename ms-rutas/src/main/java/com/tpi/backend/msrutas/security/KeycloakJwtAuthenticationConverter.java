package com.tpi.backend.msrutas.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Set;
import java.util.HashSet;



public class KeycloakJwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {

        JwtGrantedAuthoritiesConverter defaultConverter = new JwtGrantedAuthoritiesConverter();

        // 🔹 Mutable Set
        Set<GrantedAuthority> authorities = new HashSet<>(defaultConverter.convert(jwt));

        // ---------------- REALM ROLES ----------------
        Map<String, Object> realmAccess = jwt.getClaimAsMap("realm_access");
        if (realmAccess != null && realmAccess.containsKey("roles")) {

            List<String> realmRoles = (List<String>) realmAccess.get("roles");

            authorities.addAll(
                    realmRoles.stream()
                            .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                            .collect(Collectors.toList())
            );
        }

        // ---------------- CLIENT ROLES ----------------
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        if (resourceAccess != null) {
            resourceAccess.forEach((client, data) -> {
                if (data instanceof Map<?, ?> clientMap && clientMap.containsKey("roles")) {

                    List<String> clientRoles = (List<String>) clientMap.get("roles");

                    authorities.addAll(
                            clientRoles.stream()
                                    .map(r -> new SimpleGrantedAuthority("ROLE_" + r.toUpperCase()))
                                    .collect(Collectors.toList())
                    );
                }
            });
        }

        return new JwtAuthenticationToken(jwt, authorities);
    }
}
