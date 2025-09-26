package org.novacore.userservice.controller.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequestDTO {
    @NotBlank
    private String name;

    @Email
    @NotBlank(message = "Mail can not be blank")
    private String email;
}

