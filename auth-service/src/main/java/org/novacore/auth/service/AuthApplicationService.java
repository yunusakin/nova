package org.novacore.auth.service;

import org.novacore.auth.client.UserServiceClient;
import org.novacore.auth.domain.AuthUser;
import org.novacore.auth.domain.RefreshToken;
import org.novacore.auth.domain.Role;
import org.novacore.auth.repository.AuthUserRepository;
import org.novacore.auth.repository.RefreshTokenRepository;
import org.novacore.auth.repository.RoleRepository;
import org.novacore.lib.security.AuthenticationException;
import org.novacore.lib.security.JwtTokenService;
import org.novacore.lib.security.JwtTokens;
import org.novacore.lib.security.dto.AuthLoginRequest;
import org.novacore.lib.security.dto.AuthLogoutRequest;
import org.novacore.lib.security.dto.AuthRefreshRequest;
import org.novacore.lib.security.dto.AuthTokenResponse;
import org.novacore.lib.security.dto.TokenValidationResponse;
import org.novacore.lib.security.dto.UserCredentialDto;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuthApplicationService {

    private final AuthUserRepository authUserRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserServiceClient userServiceClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public AuthApplicationService(AuthUserRepository authUserRepository,
                                  RoleRepository roleRepository,
                                  RefreshTokenRepository refreshTokenRepository,
                                  UserServiceClient userServiceClient,
                                  PasswordEncoder passwordEncoder,
                                  JwtTokenService jwtTokenService) {
        this.authUserRepository = authUserRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.userServiceClient = userServiceClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    public AuthTokenResponse login(AuthLoginRequest request) {
        UserCredentialDto credentials = userServiceClient.findByEmail(request.email())
                .orElseThrow(() -> new AuthenticationException("Invalid credentials"));

        if (!"ACTIVE".equalsIgnoreCase(credentials.status())) {
            throw new AuthenticationException("User account is not active");
        }

        if (!passwordEncoder.matches(request.password(), credentials.password())) {
            throw new AuthenticationException("Invalid credentials");
        }

        AuthUser authUser = authUserRepository.findByEmailIgnoreCase(request.email())
                .orElseGet(() -> createAuthUser(credentials));

        syncUser(authUser, credentials);
        authUserRepository.save(authUser);

        JwtTokens tokens = jwtTokenService.issueTokens(
                authUser.getId().toString(),
                Map.of("email", authUser.getEmail(), "name", authUser.getName()),
                authUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        persistRefreshToken(authUser, tokens);

        return new AuthTokenResponse(
                "Bearer",
                tokens.accessToken(),
                tokens.accessTokenExpiresAt(),
                tokens.refreshToken(),
                tokens.refreshTokenExpiresAt(),
                authUser.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public AuthTokenResponse refresh(AuthRefreshRequest request) {
        RefreshToken token = refreshTokenRepository.findByToken(request.refreshToken())
                .filter(existing -> !existing.isRevoked())
                .filter(existing -> existing.getExpiresAt().isAfter(Instant.now()))
                .orElseThrow(() -> new AuthenticationException("Invalid refresh token"));

        AuthUser user = token.getUser();
        token.setRevoked(true);
        refreshTokenRepository.save(token);

        JwtTokens tokens = jwtTokenService.issueTokens(
                user.getId().toString(),
                Map.of("email", user.getEmail(), "name", user.getName()),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );

        persistRefreshToken(user, tokens);

        return new AuthTokenResponse(
                "Bearer",
                tokens.accessToken(),
                tokens.accessTokenExpiresAt(),
                tokens.refreshToken(),
                tokens.refreshTokenExpiresAt(),
                user.getRoles().stream().map(Role::getName).collect(Collectors.toSet())
        );
    }

    public void logout(AuthLogoutRequest request) {
        refreshTokenRepository.findByToken(request.refreshToken())
                .ifPresent(token -> {
                    token.setRevoked(true);
                    refreshTokenRepository.save(token);
                });
    }

    @Transactional(readOnly = true)
    public TokenValidationResponse validate(String token) {
        if (token == null || token.isBlank()) {
            return new TokenValidationResponse(false, null, Set.of(), null);
        }
        try {
            var parsed = jwtTokenService.parseToken(token);
            Instant expiresAt = parsed.getBody().getExpiration().toInstant();
            Set<String> roles = jwtTokenService.extractRoles(token);
            return new TokenValidationResponse(true, parsed.getBody().getSubject(), roles, expiresAt);
        } catch (Exception ex) {
            return new TokenValidationResponse(false, null, Set.of(), null);
        }
    }

    private AuthUser createAuthUser(UserCredentialDto credentials) {
        AuthUser user = new AuthUser();
        user.setId(credentials.id());
        user.setEmail(credentials.email());
        user.setName(credentials.name());
        user.setStatus(credentials.status());
        return authUserRepository.save(user);
    }

    private void syncUser(AuthUser authUser, UserCredentialDto credentials) {
        authUser.setName(credentials.name());
        authUser.setStatus(credentials.status());
        Set<String> roleNames = Optional.ofNullable(credentials.roles())
                .filter(roles -> !roles.isEmpty())
                .orElse(Set.of("USER"));
        Set<Role> roles = roleNames
                .stream()
                .map(String::toUpperCase)
                .map(this::ensureRoleExists)
                .collect(Collectors.toSet());
        authUser.setRoles(roles);
    }

    private Role ensureRoleExists(String roleName) {
        return roleRepository.findByNameIgnoreCase(roleName)
                .orElseGet(() -> roleRepository.save(new Role(roleName.toUpperCase(), "Auto-created role")));
    }

    private void persistRefreshToken(AuthUser authUser, JwtTokens tokens) {
        refreshTokenRepository.deleteByExpiresAtBefore(Instant.now());
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(authUser);
        refreshToken.setToken(tokens.refreshToken());
        refreshToken.setIssuedAt(Instant.now());
        refreshToken.setExpiresAt(tokens.refreshTokenExpiresAt());
        refreshTokenRepository.save(refreshToken);
    }
}
