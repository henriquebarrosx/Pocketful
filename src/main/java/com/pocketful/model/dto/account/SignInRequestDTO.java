package com.pocketful.model.dto.account;

import jakarta.validation.constraints.*;

public record SignInRequestDTO(
        @NotNull(message = "email cannot be null")
        @NotEmpty(message = "email cannot be empty")
        @NotBlank(message = "email cannot be blank")
        @Email(message = "email must to be valid")
        String email,

        @NotNull(message = "password cannot be null")
        @NotEmpty(message = "password cannot be empty")
        @NotBlank(message = "password cannot be blank")
        @Size(min = 8, message = "password must to have at least 8 characters")
        String password) { }
