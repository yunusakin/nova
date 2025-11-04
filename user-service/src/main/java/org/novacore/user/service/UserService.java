package org.novacore.user.service;

import org.novacore.lib.exceptions.BusinessException;
import org.novacore.lib.exceptions.ResourceNotFoundException;
import org.novacore.user.domain.User;
import org.novacore.user.dto.UserRequest;
import org.novacore.user.dto.UserResponse;
import org.novacore.user.messaging.UserEventPublisher;
import org.novacore.user.repository.UserRepository;
import org.novacore.user.domain.UserStatus;
import org.novacore.lib.security.dto.UserCredentialDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final UserEventPublisher eventPublisher;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserEventPublisher eventPublisher, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.eventPublisher = eventPublisher;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    public UserResponse findById(UUID id) {
        return userMapper.toResponse(getById(id));
    }

    @Transactional
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Email already in use");
        }
        if (request.password() == null || request.password().isBlank()) {
            throw new BusinessException("Password is required");
        }
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));
        applyStatus(user, request.status());
        applyRoles(user, request.roles());
        User saved = userRepository.save(user);
        eventPublisher.publishUserCreated(saved.getId(), saved.getName(), saved.getEmail());
        return userMapper.toResponse(saved);
    }

    @Transactional
    public UserResponse update(UUID id, UserRequest request) {
        User user = getById(id);
        if (!user.getEmail().equalsIgnoreCase(request.email()) && userRepository.existsByEmailIgnoreCase(request.email())) {
            throw new BusinessException("Email already in use");
        }
        userMapper.updateEntity(request, user);
        if (request.password() != null && !request.password().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.password()));
        }
        applyStatus(user, request.status());
        if (request.roles() != null) {
            applyRoles(user, request.roles());
        }
        return userMapper.toResponse(user);
    }

    @Transactional
    public void delete(UUID id) {
        User user = getById(id);
        userRepository.delete(user);
    }

    public Optional<UserCredentialDto> findCredentialsByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email)
                .map(user -> new UserCredentialDto(
                        user.getId(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getName(),
                        user.getStatus().name(),
                        Set.copyOf(user.getRoles())
                ));
    }

    private User getById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User %s not found".formatted(id)));
    }

    private void applyStatus(User user, String status) {
        if (status == null || status.isBlank()) {
            if (user.getStatus() == null) {
                user.setStatus(UserStatus.ACTIVE);
            }
            return;
        }
        try {
            user.setStatus(UserStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Invalid status value: " + status);
        }
    }

    private void applyRoles(User user, Set<String> roles) {
        Set<String> resolved = new HashSet<>();
        if (roles != null) {
            roles.stream()
                    .filter(role -> role != null && !role.isBlank())
                    .map(role -> role.trim().toUpperCase())
                    .forEach(resolved::add);
        }
        if (resolved.isEmpty()) {
            resolved.add("USER");
        }
        user.setRoles(resolved);
    }
}
