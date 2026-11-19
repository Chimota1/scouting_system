package org.example.scoutingsys.config;

import org.example.scoutingsys.annotation.ScoutAgency;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ScoutAgencyArgumentResolverTest {

    private ScoutAgencyArgumentResolver resolver;

    @Mock
    private ModelAndViewContainer mavContainer;

    @Mock
    private NativeWebRequest webRequest;

    @Mock
    private WebDataBinderFactory binderFactory;

    private static class DummyController {
        void methodWithValidParam(@ScoutAgency String agency) {}
        void methodWithInvalidTypeParam(@ScoutAgency Long id) {}
        void methodWithoutAnnotation(String agency) {}
        void methodWithCustomDefault(@ScoutAgency(defaultValue = "UEFA-Scouts") String agency) {}
    }

    @BeforeEach
    void setUp() {
        resolver = new ScoutAgencyArgumentResolver();
    }

    @Test
    @DisplayName("supportsParameter returns true for @ScoutAgency String parameter")
    void supportsParameter_withAnnotationAndStringType_returnsTrue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertTrue(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("supportsParameter returns false for @ScoutAgency non-String parameter")
    void supportsParameter_withAnnotationAndNonStringType_returnsFalse() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithInvalidTypeParam", Long.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("supportsParameter returns false when @ScoutAgency is missing")
    void supportsParameter_withoutAnnotation_returnsFalse() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithoutAnnotation", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        assertFalse(resolver.supportsParameter(parameter));
    }

    @Test
    @DisplayName("resolveArgument returns header value when X-Scout-Agency is present")
    void resolveArgument_headerPresent_returnsHeaderValue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        when(webRequest.getHeader("X-Scout-Agency")).thenReturn("RealMadrid-Academy");

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("RealMadrid-Academy", result);
    }

    @Test
    @DisplayName("resolveArgument returns default value when X-Scout-Agency header is missing")
    void resolveArgument_headerMissing_returnsDefaultValue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        when(webRequest.getHeader("X-Scout-Agency")).thenReturn(null);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("Independent-Scout", result);
    }

    @Test
    @DisplayName("resolveArgument returns default value when X-Scout-Agency header is blank")
    void resolveArgument_headerBlank_returnsDefaultValue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithValidParam", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        when(webRequest.getHeader("X-Scout-Agency")).thenReturn("   ");

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("Independent-Scout", result);
    }

    @Test
    @DisplayName("resolveArgument returns custom default value from annotation when header is missing")
    void resolveArgument_customDefault_returnsCustomDefaultValue() throws NoSuchMethodException {
        Method method = DummyController.class.getDeclaredMethod("methodWithCustomDefault", String.class);
        MethodParameter parameter = new MethodParameter(method, 0);

        when(webRequest.getHeader("X-Scout-Agency")).thenReturn(null);

        Object result = resolver.resolveArgument(parameter, mavContainer, webRequest, binderFactory);

        assertEquals("UEFA-Scouts", result);
    }
}
