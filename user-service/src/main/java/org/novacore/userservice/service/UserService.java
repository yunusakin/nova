package org.novacore.userservice.service;

import lombok.RequiredArgsConstructor;
import org.novacore.novalib.events.UserCreatedEvent;
import org.novacore.userservice.controller.dto.UserRequestDTO;
import org.novacore.userservice.controller.dto.UserResponseDTO;
import org.novacore.userservice.domain.User;
import org.novacore.userservice.events.UserEventPublisher;
import org.springframework.stereotype.Service;
import org.novacore.userservice.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository repo;
    private final UserEventPublisher userEventPublisher;

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    private User toEntity(UserRequestDTO dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public List<UserResponseDTO> getAllUsers() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public Optional<UserResponseDTO> getUserById(Long id) {
        return repo.findById(id).map(this::toResponse);
    }


    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        repo.deleteById(id);
    }
    public UserResponseDTO createUser(UserRequestDTO dto) {
        repo.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        });
        User user = repo.save(toEntity(dto));
        UserResponseDTO response = toResponse(user);

        userEventPublisher.publishUserCreated(
                new UserCreatedEvent(user.getId(), user.getName(), user.getEmail())
        );

        return response;
    }

}

