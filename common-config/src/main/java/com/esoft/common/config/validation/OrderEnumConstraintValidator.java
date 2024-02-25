package com.esoft.common.config.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;

public class OrderEnumConstraintValidator implements ConstraintValidator<OrderEnumValidValue, String> {
    private Class<? extends Enum<?>> enumClass;

    @Override
    public void initialize(OrderEnumValidValue orderEnumValidValue) {
        this.enumClass = orderEnumValidValue.enumClass();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Null values are considered valid
        }

        Enum<?>[] enumConstants = enumClass.getEnumConstants();

        // Value is valid for at least one dynamic enum
        return Arrays.stream(enumConstants).anyMatch(e -> e.name().equalsIgnoreCase(value));
    }
}
