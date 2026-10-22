package org.example.scoutingsys.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final ScoutAgencyArgumentResolver scoutAgencyArgumentResolver;

    public WebMvcConfig(ScoutAgencyArgumentResolver scoutAgencyArgumentResolver) {
        this.scoutAgencyArgumentResolver = scoutAgencyArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(scoutAgencyArgumentResolver);
    }
}
