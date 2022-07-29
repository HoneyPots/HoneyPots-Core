package com.honeypot.common.validation.constraints;

import com.honeypot.common.validation.validators.AllowedSortPropertiesValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = AllowedSortPropertiesValidator.class)
@Target({ElementType.METHOD, PARAMETER})
@Retention(RUNTIME)
public @interface AllowedSortProperties {

    String message() default "{com.honeypot.common.validation.constraints.AllowedSortProperties.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String[] value();

    @Target({ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        AllowedSortProperties[] value();
    }

}