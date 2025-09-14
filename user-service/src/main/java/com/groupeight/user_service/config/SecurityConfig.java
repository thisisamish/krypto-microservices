package com.groupeight.user_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;

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
				.authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**",
						"/swagger-ui.html", "/actuator/**").permitAll().anyRequest().authenticated())
				.oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter())));
		return http.build();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
		return cfg.getAuthenticationManager();
	}

	private Converter<Jwt, ? extends AbstractAuthenticationToken> jwtAuthConverter() {
		var roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName("roles"); // we’ll put roles here
		roles.setAuthorityPrefix("ROLE_"); // Spring expects ROLE_*
		var conv = new JwtAuthenticationConverter();
		conv.setJwtGrantedAuthoritiesConverter(roles);
		return conv;
	}
}
