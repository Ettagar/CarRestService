package ua.foxminded.carrestservice.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Value("${auth0.domain}")
	private String domain;

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
		.csrf(csrf -> csrf.disable())
		.authorizeHttpRequests(authorize -> authorize
				.requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/api-docs.yaml").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/v1/**").permitAll()
				.requestMatchers(HttpMethod.POST, "/api/v1/**").hasAuthority("write:cars")
				.requestMatchers(HttpMethod.PUT, "/api/v1/**").hasAuthority("write:cars")
				.requestMatchers(HttpMethod.DELETE, "/api/v1/**").hasAuthority("write:cars")
				.requestMatchers(HttpMethod.PATCH, "/api/v1/**").hasAuthority("write:cars")
				.anyRequest().authenticated()
				)
		.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
		.oauth2ResourceServer(oauth2 -> oauth2
				.jwt(jwt -> jwt
						.jwkSetUri(String.format("https://%s/.well-known/jwks.json", domain))
						)
				);

		return http.build();
	}

	@Bean
	JwtDecoder jwtDecoder() {
		String jwkSetUri = String.format("https://%s/.well-known/jwks.json", domain);
		return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
	}
}
