package com.example.quizz.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = StrongPasswordValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface StrongPassword {
    String message() default "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean requireUppercase() default true;

    boolean requireLowercase() default true;

    boolean requireDigit() default true;

    boolean requireSpecialChar() default true;

    int minLength() default 8;
}
