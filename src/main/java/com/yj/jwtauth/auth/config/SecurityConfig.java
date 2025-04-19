package com.yj.jwtauth.auth.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.jwtauth.auth.filter.JwtAuthenticationFilter;
import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.common.exception.error.ErrorResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final ObjectMapper objectMapper;

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http
			.csrf(csrf -> csrf.disable())
			.httpBasic(httpBasic -> httpBasic.disable())
			.formLogin(formLogin -> formLogin.disable())
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(
					"/api/users/signup",
					"/api/admins/signup",
					"/api/signin",
					"/swagger-ui/**",
					"/swagger-ui.html",
					"/v3/api-docs/**",
					"/swagger-resources/**",
					"/webjars/**",
					"/configuration/**"
				).permitAll()
				.requestMatchers("/api/my-informations").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/api/admins/**").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(ex -> ex
				.authenticationEntryPoint((request, response, authException) -> {
					sendErrorResponse(response, ErrorCode.INVALID_TOKEN);
				})
				.accessDeniedHandler((request, response, accessDeniedException) -> {
					sendErrorResponse(response, ErrorCode.ACCESS_DENIED);
				})
			);

		return http.build();
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json; charset=UTF-8");
		ErrorResponseDto errorResponse = new ErrorResponseDto(errorCode);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}
}