package org.novacore.auth.client;

import org.novacore.lib.security.dto.UserCredentialDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
public class UserServiceClient {

    private final RestClient restClient;

    public UserServiceClient(@Value("${nova.clients.user-service.base-url}") String baseUrl,
                             RestClient.Builder builder) {
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<UserCredentialDto> findByEmail(String email) {
        try {
            ResponseEntity<UserCredentialDto> response = restClient.get()
                    .uri("/internal/users/email/{email}", email)
                    .retrieve()
                    .toEntity(UserCredentialDto.class);
            return Optional.ofNullable(response.getBody());
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}
