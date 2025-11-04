package org.novacore.user.controller;

import org.novacore.lib.api.ApiResponse;
import org.novacore.lib.security.dto.UserCredentialDto;
import org.novacore.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;

@RestController
@RequestMapping("/internal/users")
public class InternalUserController {

    private final UserService userService;

    public InternalUserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserCredentialDto>> findByEmail(@PathVariable String email) {
        return userService.findCredentialsByEmail(email)
                .map(credentials -> ResponseEntity.ok(ApiResponse.success(credentials)))
                .orElseGet(() -> ResponseEntity.status(404)
                        .body(new ApiResponse<>(false, null, "User not found", Instant.now())));
    }
}
