package com.honeypot.common.validation.validators;

import com.honeypot.common.validation.constraints.Enum;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EnumValidator implements ConstraintValidator<Enum, CharSequence> {
    private Set<String> enumValues;

    @Override
    public void initialize(Enum constraintAnnotation) {
        enumValues = Stream.of(constraintAnnotation.target().getEnumConstants())
                .map(java.lang.Enum::name)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext context) {
        if (value == null) {
            return false;
        }
        return enumValues.contains(value.toString().toUpperCase());
    }
}
