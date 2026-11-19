package org.example.scoutingsys.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class WebMvcConfigTest {

    @Mock
    private ScoutAgencyArgumentResolver scoutAgencyArgumentResolver;

    @Test
    @DisplayName("addArgumentResolvers: registers ScoutAgencyArgumentResolver in Spring MVC resolver list")
    void addArgumentResolvers_addsScoutAgencyArgumentResolverToList() {
        WebMvcConfig webMvcConfig = new WebMvcConfig(scoutAgencyArgumentResolver);
        List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();

        webMvcConfig.addArgumentResolvers(resolvers);

        assertEquals(1, resolvers.size());
        assertTrue(resolvers.contains(scoutAgencyArgumentResolver));
    }
}
