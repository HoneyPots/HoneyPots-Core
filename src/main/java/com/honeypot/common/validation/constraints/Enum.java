package com.honeypot.common.validation.constraints;

import com.honeypot.common.validation.validators.EnumValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Target({FIELD})
@Retention(RUNTIME)
@Repeatable(Enum.List.class)
@Constraint(validatedBy = {EnumValidator.class})
public @interface Enum {

    String message() default "{com.honeypot.common.validation.constraints.Enum.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends java.lang.Enum<?>> target();

    @Documented
    @Target(FIELD)
    @Retention(RUNTIME)
    @interface List {
        Enum[] value();
    }
}
