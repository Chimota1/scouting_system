package org.example.scoutingsys.config;

import org.example.scoutingsys.annotation.ScoutAgency;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class ScoutAgencyArgumentResolver implements HandlerMethodArgumentResolver {

    public static final String HEADER_NAME = "X-Scout-Agency";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(ScoutAgency.class) &&
                parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        ScoutAgency annotation = parameter.getParameterAnnotation(ScoutAgency.class);
        String defaultValue = (annotation != null) ? annotation.defaultValue() : "Independent-Scout";

        String headerValue = webRequest.getHeader(HEADER_NAME);
        if (headerValue == null || headerValue.trim().isEmpty()) {
            return defaultValue;
        }

        return headerValue.trim();
    }
}
