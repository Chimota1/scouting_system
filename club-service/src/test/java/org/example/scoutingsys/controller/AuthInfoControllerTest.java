package org.example.scoutingsys.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthInfoControllerTest {

    @Mock
    private OAuth2AuthorizedClientRepository authorizedClientRepository;

    @Mock
    private OAuth2AuthorizedClient authorizedClient;

    @Mock
    private OAuth2AccessToken accessToken;

    @Mock
    private HttpServletRequest request;

    @Mock
    private OAuth2AuthenticationToken authentication;

    @InjectMocks
    private AuthInfoController authInfoController;

    @Test
    @DisplayName("AC 8: getToken uses resolved scoutAgency in response body")
    void getToken_authenticated_returnsTokenAndUsesResolvedAgency() {
        when(authentication.getAuthorizedClientRegistrationId()).thenReturn("keycloak");
        when(authentication.getName()).thenReturn("test_user");
        when(authorizedClientRepository.loadAuthorizedClient("keycloak", authentication, request))
                .thenReturn(authorizedClient);
        when(authorizedClient.getAccessToken()).thenReturn(accessToken);
        when(accessToken.getTokenValue()).thenReturn("token-abc-123");

        Map<String, Object> response = authInfoController.getToken(authentication, request, "Dynamo-Scouting-Agency");

        assertNotNull(response);
        assertEquals(true, response.get("authenticated"));
        assertEquals("token-abc-123", response.get("token"));
        assertEquals("Dynamo-Scouting-Agency", response.get("scoutAgency"));
    }

    @Test
    @DisplayName("getToken: returns error when authentication is null")
    void getToken_nullAuthentication_returnsError() {
        Map<String, Object> response = authInfoController.getToken(null, request, "Independent-Scout");

        assertEquals(false, response.get("authenticated"));
        assertTrue(response.containsKey("error"));
    }
}
