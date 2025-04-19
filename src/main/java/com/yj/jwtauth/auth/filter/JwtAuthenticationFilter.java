package com.yj.jwtauth.auth.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.jwtauth.auth.JwtService;
import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.common.exception.error.ErrorResponseDto;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Order(SecurityProperties.DEFAULT_FILTER_ORDER - 1)
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
	private final JwtService jwtService;
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final AntPathMatcher pathMatcher = new AntPathMatcher();

	private static final String[] whiteList = {
		"/api/users/signup",
		"/api/admins/signup",
		"/api/signin",
		"/swagger-ui/**",
		"/swagger-ui.html",
		"/v3/api-docs/**",
		"/v3/api-docs.yaml",
		"/swagger-resources/**",
		"/webjars/**",
		"/configuration/**",
		"/error" // 추가: /error 경로 화이트리스트
	};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
		throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String requestURI = httpRequest.getRequestURI();
		String contextPath = httpRequest.getContextPath();
		if (contextPath != null && !contextPath.isEmpty() && requestURI.startsWith(contextPath)) {
			requestURI = requestURI.substring(contextPath.length());
		}
		if (requestURI.contains("?")) {
			requestURI = requestURI.substring(0, requestURI.indexOf("?"));
		}

		logger.debug("Processing request URI: {}", requestURI);

		if (isWhiteListPath(requestURI)) {
			logger.debug("URI {} is whitelisted, bypassing JWT validation", requestURI);
			chain.doFilter(request, response);
			return;
		}

		String authorizationHeader = httpRequest.getHeader("Authorization");
		logger.debug("Authorization header: {}", authorizationHeader);

		if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
			logger.warn("Missing or invalid Authorization header for URI: {}", requestURI);
			sendErrorResponse(httpResponse, ErrorCode.INVALID_TOKEN);
			return;
		}

		String token = authorizationHeader.substring(7);
		logger.debug("Extracted token: {}", token);

		try {
			DecodedJWT decodedJwt = jwtService.verifyToken(token);
			String userId = decodedJwt.getSubject();
			List<String> roles = decodedJwt.getClaim("roles").asList(String.class);
			List<SimpleGrantedAuthority> authorities = roles.stream()
			.map(SimpleGrantedAuthority::new)
			.collect(Collectors.toList());

			String email = decodedJwt.getClaim("email").asString();

			logger.debug("Token verified, userId: {}, roles: {}, email: {}", userId, roles, email);

			UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				email,
				null,
				authorities
			);
			SecurityContextHolder.getContext().setAuthentication(authToken);
			logger.debug("Set Authentication in SecurityContext with authorities: {}", authorities);

			httpRequest.setAttribute("userId", Long.parseLong(userId));
			logger.debug("Set userId attribute: {}", userId);

			chain.doFilter(request, response);
		} catch (Exception e) {
			logger.error("Token verification failed for URI: {}, error: {}", requestURI, e.getMessage());
			sendErrorResponse(httpResponse, ErrorCode.INVALID_TOKEN);
		}
	}

	private void sendErrorResponse(HttpServletResponse response, ErrorCode errorCode) throws IOException {
		response.setStatus(errorCode.getStatus().value());
		response.setContentType("application/json; charset=UTF-8");
		ErrorResponseDto errorResponse = new ErrorResponseDto(errorCode);
		response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
	}

	private boolean isWhiteListPath(String requestURI) {
		boolean isWhitelisted = Arrays.stream(whiteList)
			.anyMatch(pattern -> {
				boolean matches = pathMatcher.match(pattern, requestURI);
				logger.trace("Matching URI: {} against pattern: {}, result: {}", requestURI, pattern, matches);
				return matches;
			});
		logger.debug("Checking whitelist for URI: {}, isWhitelisted: {}", requestURI, isWhitelisted);
		return isWhitelisted;
	}
}