package org.novacore.auth.controller;

import jakarta.validation.Valid;
import org.novacore.auth.service.AuthApplicationService;
import org.novacore.lib.api.ApiResponse;
import org.novacore.lib.security.dto.AuthLoginRequest;
import org.novacore.lib.security.dto.AuthLogoutRequest;
import org.novacore.lib.security.dto.AuthRefreshRequest;
import org.novacore.lib.security.dto.AuthTokenResponse;
import org.novacore.lib.security.dto.TokenValidationResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthApplicationService authApplicationService;

    public AuthController(AuthApplicationService authApplicationService) {
        this.authApplicationService = authApplicationService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthTokenResponse tokens = authApplicationService.login(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenResponse>> refresh(@Valid @RequestBody AuthRefreshRequest request) {
        AuthTokenResponse tokens = authApplicationService.refresh(request);
        return ResponseEntity.ok(ApiResponse.success(tokens));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody AuthLogoutRequest request) {
        authApplicationService.logout(request);
        return ResponseEntity.ok(ApiResponse.successMessage("Logout successful"));
    }

    @GetMapping("/validate")
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validate(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorizationHeader) {
        String token = extractBearerToken(authorizationHeader);
        TokenValidationResponse validation = authApplicationService.validate(token);
        return ResponseEntity.ok(ApiResponse.success(validation));
    }

    private String extractBearerToken(String header) {
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }
}
