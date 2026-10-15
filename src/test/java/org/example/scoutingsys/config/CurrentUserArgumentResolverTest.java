package org.example.scoutingsys.config;

import org.example.scoutingsys.annotation.CurrentUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrentUserArgumentResolverTest {

    private CurrentUserArgumentResolver resolver;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    // Helper controller for MethodParameter reflection
    private static class DummyController {
        void methodWithValidParam(@CurrentUser String username) {}
        void methodWithInvalidTypeParam(@CurrentUser Long id) {}
        void methodWithoutAnnotation(String username) {}
    }

    @BeforeEach
    void setUp() {
        resolver = new CurrentUserArgumentResolver();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // --- supportsParameter tests ---

    @Test
    @DisplayName("supportsParameter: повертає true, якщо параметр має анотацію @CurrentUser та тип String")
    void supportsParameter_withCurrentUserAnnotationAndStringType_returnsTrue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("supportsParameter: повертає false, якщо параметр має анотацію @CurrentUser, але тип не String")
    void supportsParameter_withCurrentUserAnnotationAndNonStringType_returnsFalse() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithInvalidTypeParam", Long.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("supportsParameter: повертає false, якщо анотація @CurrentUser відсутня")
    void supportsParameter_withoutAnnotation_returnsFalse() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithoutAnnotation", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    // --- resolveArgument tests ---

    @Test
    @DisplayName("resolveArgument: повертає 'Guest', якщо автентифікація відсутня в SecurityContext")
    void resolveArgument_noAuthenticationInContext_returnsGuest() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("Guest", result);
    }

    @Test
    @DisplayName("resolveArgument: повертає 'Guest', якщо користувач не автентифікований")
    void resolveArgument_unauthenticated_returnsGuest() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("Guest", result);
    }

    @Test
    @DisplayName("resolveArgument: повертає preferredUsername для користувача OidcUser")
    void resolveArgument_oidcUserPrincipal_returnsPreferredUsername() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        Authentication auth = mock(Authentication.class);
        OidcUser oidcUser = mock(OidcUser.class);
        when(oidcUser.getPreferredUsername()).thenReturn("john_doe");
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getPrincipal()).thenReturn(oidcUser);
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("john_doe", result);
    }

    @Test
    @DisplayName("resolveArgument: повертає ім'я з клейму preferred_username для JwtAuthenticationToken")
    void resolveArgument_jwtAuthenticationTokenWithPreferredUsername_returnsTokenUsername() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        JwtAuthenticationToken jwtToken = mock(JwtAuthenticationToken.class);
        when(jwtToken.isAuthenticated()).thenReturn(true);
        when(jwtToken.getTokenAttributes()).thenReturn(Map.of("preferred_username", "jwt_user"));
        SecurityContextHolder.getContext().setAuthentication(jwtToken);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("jwt_user", result);
    }

    @Test
    @DisplayName("resolveArgument: повертає фолбек-ім'я з getName() для JwtAuthenticationToken без preferred_username")
    void resolveArgument_jwtAuthenticationTokenWithoutPreferredUsername_returnsFallbackName() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        JwtAuthenticationToken jwtToken = mock(JwtAuthenticationToken.class);
        when(jwtToken.isAuthenticated()).thenReturn(true);
        when(jwtToken.getTokenAttributes()).thenReturn(Collections.emptyMap());
        when(jwtToken.getName()).thenReturn("fallback_user");
        SecurityContextHolder.getContext().setAuthentication(jwtToken);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("fallback_user", result);
    }

    @Test
    @DisplayName("resolveArgument: повертає ім'я через getName() для стандартної автентифікації")
    void resolveArgument_standardAuthentication_returnsName() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);
        when(auth.getName()).thenReturn("standard_user");
        SecurityContextHolder.getContext().setAuthentication(auth);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("standard_user", result);
    }
}
