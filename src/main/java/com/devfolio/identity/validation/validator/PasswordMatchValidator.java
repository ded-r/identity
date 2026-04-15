package com.devfolio.identity.validation.validator;

import com.devfolio.identity.dto.request.RegisterRequest;
import com.devfolio.identity.validation.annotation.PasswordMatch;
import jakarta.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements jakarta.validation.ConstraintValidator<PasswordMatch, RegisterRequest> {

    @Override
    public void initialize(PasswordMatch constraintAnnotation) {
    }
    public boolean isValid(RegisterRequest request, final ConstraintValidatorContext context) {
        String password = request.getPassword();
        String confirmPassword = request.getConfirmPassword();

        return password != null && password.equals(confirmPassword);
    }

}
