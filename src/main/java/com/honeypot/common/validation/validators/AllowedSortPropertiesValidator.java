package com.honeypot.common.validation.validators;

import com.honeypot.common.validation.constraints.AllowedSortProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AllowedSortPropertiesValidator implements ConstraintValidator<AllowedSortProperties, Pageable> {


    private Set<String> allowedSortProperties;

    @Override
    public void initialize(AllowedSortProperties constraintAnnotation) {
        this.allowedSortProperties = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }

        if (allowedSortProperties.isEmpty()) {
            return true;
        }

        return value.getSort()
                .stream()
                .allMatch(o -> allowedSortProperties.contains(o.getProperty()));
    }
}
