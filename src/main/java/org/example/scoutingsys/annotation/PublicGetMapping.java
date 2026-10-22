package org.example.scoutingsys.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@RequestMapping(method = RequestMethod.GET)
@ResponseStatus(HttpStatus.OK)
@CrossOrigin(origins = "*")
public @interface PublicGetMapping {

    @AliasFor(annotation = RequestMapping.class, attribute = "value")
    String[] value() default {};
}
