package org.novacore.user.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserRequest(
        @NotBlank(message = "Name is required") String name,
        @Email(message = "Email must be valid") @NotBlank(message = "Email is required") String email,
        @Size(min = 8, message = "Password must be at least 8 characters") String password,
        Set<String> roles,
        String status
) {
}
