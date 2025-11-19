package org.novacore.auth.client;

import org.novacore.lib.api.ApiResponse;
import org.novacore.lib.security.dto.UserCredentialDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.Optional;

@Component
public class UserServiceClient {

    private static final ParameterizedTypeReference<ApiResponse<UserCredentialDto>> RESPONSE_TYPE =
            new ParameterizedTypeReference<>() {
            };

    private final RestClient restClient;

    private final String internalSecret;

    public UserServiceClient(@Value("${nova.clients.user-service.base-url}") String baseUrl,
                             @Value("${nova.internal.shared-secret}") String internalSecret,
                             RestClient.Builder builder) {
        this.internalSecret = internalSecret;
        this.restClient = builder.baseUrl(baseUrl).build();
    }

    public Optional<UserCredentialDto> findByEmail(String email) {
        try {
            ApiResponse<UserCredentialDto> response = restClient.get()
                    .uri("/internal/users/email/{email}", email)
                    .header("X-Internal-Secret", internalSecret)
                    .retrieve()
                    .body(RESPONSE_TYPE);
            if (response == null || !response.success()) {
                return Optional.empty();
            }
            return Optional.ofNullable(response.data());
        } catch (RestClientResponseException ex) {
            if (ex.getStatusCode().value() == 404) {
                return Optional.empty();
            }
            throw ex;
        }
    }
}
