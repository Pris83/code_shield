package za.co.turbo.code_shield.model;

import jakarta.validation.constraints.*;

public record AuthRequest(
        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @NotBlank(message = "Password is required")
        String password,

        @Email(message = "Email must be valid")
        @NotBlank(message = "Email is required")
        String email
) {}

