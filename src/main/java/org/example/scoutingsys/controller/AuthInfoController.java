package org.example.scoutingsys.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthInfoController {

    @Autowired
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @GetMapping("/auth/token")
    public Map<String, Object> getToken(OAuth2AuthenticationToken authentication, HttpServletRequest request) {
        if (authentication == null) {
            return Map.of("authenticated", false, "error", "Немає сесії в Spring Security");
        }

        OAuth2AuthorizedClient client = authorizedClientRepository.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication,
                request);

        if (client == null || client.getAccessToken() == null) {
            return Map.of("authenticated", false, "error", "Токен не знайдено в репозиторії сесії");
        }

        OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
        String username = oidcUser.getPreferredUsername() != null ? oidcUser.getPreferredUsername() : oidcUser.getName();

        return Map.of(
                "authenticated", true,
                "token", client.getAccessToken().getTokenValue(),
                "username", username
        );
    }
}