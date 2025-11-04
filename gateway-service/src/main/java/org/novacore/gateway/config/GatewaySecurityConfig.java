package org.novacore.gateway.config;

import org.novacore.gateway.security.BearerTokenAuthenticationConverter;
import org.novacore.gateway.security.JwtAuthenticationManager;
import org.novacore.lib.security.JwtTokenService;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Configuration
public class GatewaySecurityConfig {

    private static final String AUTHORIZED_PATH_PATTERN = "/api/**";

    @Bean
    public ReactiveAuthenticationManager jwtReactiveAuthenticationManager(JwtTokenService tokenService) {
        return new JwtAuthenticationManager(tokenService);
    }

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http,
                                                            ReactiveAuthenticationManager authenticationManager) {
        AuthenticationWebFilter jwtFilter = new AuthenticationWebFilter(authenticationManager);
        jwtFilter.setServerAuthenticationConverter(new BearerTokenAuthenticationConverter());
        jwtFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(AUTHORIZED_PATH_PATTERN));

        http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/auth/**", "/actuator/**").permitAll()
                        .anyExchange().authenticated());

        return http.build();
    }

    @Bean
    public GlobalFilter propagateAuthenticationHeaders() {
        return (exchange, chain) -> exchange.getPrincipal()
                .cast(Authentication.class)
                .flatMap(authentication -> chain.filter(mutateRequest(exchange, authentication)))
                .switchIfEmpty(chain.filter(exchange));
    }

    private ServerWebExchange mutateRequest(ServerWebExchange exchange, Authentication authentication) {
        String roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        return exchange.mutate()
                .request(builder -> builder
                        .header("X-User-Id", authentication.getName())
                        .header("X-User-Roles", roles))
                .build();
    }
}
