package com.groupeight.api_gateway.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewaySecurity {

	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:4200"));
		config.setAllowCredentials(true);
		config.setAllowedHeaders(
				List.of("Authorization", "Content-Type", "X-XSRF-TOKEN", "X-Requested-With", "Accept"));
		config.setExposedHeaders(List.of("Authorization", "Set-Cookie", "Content-Type"));
		config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	SecurityFilterChain filter(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**")
				.permitAll().requestMatchers(HttpMethod.GET, "/api/v1/products", "/api/v1/products/**").permitAll()
				.requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/actuator/**")
				.permitAll().anyRequest().authenticated()).oauth2ResourceServer(oauth -> oauth.jwt());
		return http.build();
	}
}
