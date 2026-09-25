package org.example.scoutingsys.security;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class CustomOidcUserService extends OidcUserService {

    @Value("${jwt.auth.converter.resource-id}")
    private String resourceId;

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        OidcUser oidcUser = super.loadUser(userRequest);

        Set<GrantedAuthority> authorities = new HashSet<>(oidcUser.getAuthorities());
        authorities.addAll(extractResourceRoles(userRequest.getAccessToken().getTokenValue()));

        return new DefaultOidcUser(authorities, oidcUser.getIdToken(), oidcUser.getUserInfo());
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> extractResourceRoles(String accessTokenValue) {
        try {
            JWTClaimsSet claims = JWTParser.parse(accessTokenValue).getJWTClaimsSet();

            Object resourceAccessObj = claims.getClaim("resource_access");
            if (!(resourceAccessObj instanceof Map<?, ?> resourceAccess)) {
                return Set.of();
            }

            Object resourceObj = ((Map<String, Object>) resourceAccess).get(resourceId);
            if (!(resourceObj instanceof Map<?, ?> resource)) {
                return Set.of();
            }

            Object rolesObj = ((Map<String, Object>) resource).get("roles");
            if (!(rolesObj instanceof Collection<?> roles)) {
                return Set.of();
            }

            return roles.stream()
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role.toString().toUpperCase()))
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            return Set.of();
        }
    }
}