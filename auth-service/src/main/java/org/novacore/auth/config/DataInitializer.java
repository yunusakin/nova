package org.novacore.auth.config;

import org.novacore.auth.domain.Role;
import org.novacore.auth.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedDefaultRoles(RoleRepository roleRepository) {
        return args -> {
            List<String> defaultRoles = List.of("ADMIN", "STAFF", "COURIER", "USER");
            for (String roleName : defaultRoles) {
                roleRepository.findByNameIgnoreCase(roleName)
                        .orElseGet(() -> roleRepository.save(new Role(roleName, roleName + " role")));
            }
        };
    }
}
