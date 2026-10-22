package org.example.scoutingsys.annotation;

import org.example.scoutingsys.controller.ClubController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

class PublicGetMappingTest {

    @Test
    @DisplayName("AC 4: PublicGetMapping combines @RequestMapping(GET), @ResponseStatus(OK), and @CrossOrigin")
    void publicGetMapping_combinesWebAnnotations() {
        RequestMapping requestMapping = PublicGetMapping.class.getAnnotation(RequestMapping.class);
        assertNotNull(requestMapping, "PublicGetMapping must contain @RequestMapping");
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, requestMapping.method());

        ResponseStatus responseStatus = PublicGetMapping.class.getAnnotation(ResponseStatus.class);
        assertNotNull(responseStatus, "PublicGetMapping must contain @ResponseStatus");
        assertEquals(HttpStatus.OK, responseStatus.value());

        CrossOrigin crossOrigin = PublicGetMapping.class.getAnnotation(CrossOrigin.class);
        assertNotNull(crossOrigin, "PublicGetMapping must contain @CrossOrigin");
        assertArrayEquals(new String[]{"*"}, crossOrigin.origins());
    }

    @Test
    @DisplayName("AC 5: Controller method with @PublicGetMapping resolves merged routing, status, and CORS")
    void publicGetMapping_onControllerMethod_resolvesMergedAnnotations() throws NoSuchMethodException {
        Method getAllMethod = ClubController.class.getMethod("getAll");

        assertTrue(getAllMethod.isAnnotationPresent(PublicGetMapping.class));

        RequestMapping mergedRequestMapping = AnnotatedElementUtils.findMergedAnnotation(getAllMethod, RequestMapping.class);
        assertNotNull(mergedRequestMapping);
        assertArrayEquals(new RequestMethod[]{RequestMethod.GET}, mergedRequestMapping.method());

        ResponseStatus mergedResponseStatus = AnnotatedElementUtils.findMergedAnnotation(getAllMethod, ResponseStatus.class);
        assertNotNull(mergedResponseStatus);
        assertEquals(HttpStatus.OK, mergedResponseStatus.value());

        CrossOrigin mergedCrossOrigin = AnnotatedElementUtils.findMergedAnnotation(getAllMethod, CrossOrigin.class);
        assertNotNull(mergedCrossOrigin);
        assertArrayEquals(new String[]{"*"}, mergedCrossOrigin.origins());
    }
}
