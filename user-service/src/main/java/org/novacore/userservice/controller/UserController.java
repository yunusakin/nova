package org.novacore.userservice.controller;

import jakarta.validation.Valid;
import org.novacore.novalib.api.ApiResponse;
import org.novacore.userservice.controller.dto.UserRequestDTO;
import org.novacore.userservice.controller.dto.UserResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.novacore.userservice.service.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping
    public ApiResponse<List<UserResponseDTO>> getAll() {
        return ApiResponse.ok(service.getAllUsers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getById(@PathVariable Long id) {
        return service.getUserById(id)
                .map(dto -> ResponseEntity.ok(ApiResponse.ok(dto)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponseDTO>> create(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO saved = service.createUser(dto);
        return ResponseEntity.created(URI.create("/api/users/" + saved.getId()))
                .body(ApiResponse.ok(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        try {
            service.deleteUser(id);
            return ResponseEntity.ok(ApiResponse.ok(null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail(e.getMessage()));
        }
    }
}
