package com.example.quizz.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class StrongPasswordValidator implements ConstraintValidator<StrongPassword, String> {

    private boolean requireUppercase;
    private boolean requireLowercase;
    private boolean requireDigit;
    private boolean requireSpecialChar;
    private int minLength;

    @Override
    public void initialize(StrongPassword constraintAnnotation) {
        this.requireUppercase = constraintAnnotation.requireUppercase();
        this.requireLowercase = constraintAnnotation.requireLowercase();
        this.requireDigit = constraintAnnotation.requireDigit();
        this.requireSpecialChar = constraintAnnotation.requireSpecialChar();
        this.minLength = constraintAnnotation.minLength();
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (password == null || password.length() < minLength) {
            return false;
        }

        if (requireUppercase && !password.matches(".*[A-Z].*")) {
            return false;
        }

        if (requireLowercase && !password.matches(".*[a-z].*")) {
            return false;
        }

        if (requireDigit && !password.matches(".*\\d.*")) {
            return false;
        }

        if (requireSpecialChar && !password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) {
            return false;
        }

        return true;
    }
}
