package service;

import controller.dto.UserRequestDTO;
import controller.dto.UserResponseDTO;
import domain.User;
import org.springframework.stereotype.Service;
import repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

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

    public UserResponseDTO createUser(UserRequestDTO dto) {
        repo.findByEmail(dto.getEmail()).ifPresent(u -> {
            throw new IllegalArgumentException("Email already in use: " + dto.getEmail());
        });
        User saved = repo.save(toEntity(dto));
        return toResponse(saved);
    }

    public void deleteUser(Long id) {
        if (!repo.existsById(id)) {
            throw new IllegalArgumentException("User not found: " + id);
        }
        repo.deleteById(id);
    }
}

