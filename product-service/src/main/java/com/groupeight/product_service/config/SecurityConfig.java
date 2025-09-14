package com.groupeight.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

	// --- H2 console: permit all (dev only) ---
	@Bean
	@Order(0)
	SecurityFilterChain h2ConsoleBean(HttpSecurity http) throws Exception {
		http.securityMatcher(new AntPathRequestMatcher("/h2-console/**"))
				.authorizeHttpRequests(a -> a.anyRequest().permitAll()).csrf(csrf -> csrf.disable()) // or
																										// .ignoringRequestMatchers("/h2-console/**")
				.headers(h -> h.frameOptions(f -> f.sameOrigin())); // H2 uses frames
		return http.build();
	}

	// --- REST API: JWT protected ---
	@Bean
	@Order(1)
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable())
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
						.permitAll().requestMatchers(HttpMethod.GET, "/api/v1/products/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v1/products").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v1/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v1/products/**").hasRole("ADMIN").anyRequest()
						.authenticated())
				.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));
		return http.build();
	}

	private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
		var roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName("roles");
		roles.setAuthorityPrefix("ROLE_");
		var conv = new JwtAuthenticationConverter();
		conv.setJwtGrantedAuthoritiesConverter(roles);
		return conv;
	}
}
