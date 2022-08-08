package com.honeypot.common.validation.validators;

import com.honeypot.common.validation.constraints.Enum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<Enum, CharSequence> {
    private Set<String> enumValues;

    private boolean validIfNull;

    @Override
    public void initialize(Enum constraintAnnotation) {
        enumValues = Stream.of(constraintAnnotation.target().getEnumConstants())
                .map(java.lang.Enum::name)
                .collect(Collectors.toSet());

        validIfNull = constraintAnnotation.ifNull();
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return validIfNull;
        }
        return enumValues.contains(value.toString().toUpperCase());
    }
}
