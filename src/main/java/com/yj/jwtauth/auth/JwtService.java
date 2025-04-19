package com.yj.jwtauth.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.yj.jwtauth.common.exception.CustomRuntimeException;
import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class JwtService {

	private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

	@Value("${jwt.secret.key}")
	private String secretKey;

	@Value("${jwt.expiration.time}")
	private Long expirationTime;

	@PostConstruct
	public void init() {
		logger.info("JWT Secret Key: {}", secretKey); // 시크릿 키 확인
		logger.info("JWT Expiration Time: {} minutes", expirationTime); // 만료 시간 확인
		if (secretKey == null || secretKey.isEmpty()) {
			logger.error("JWT secret key is not configured");
			throw new IllegalArgumentException("비밀 키 미설정");
		}
		logger.debug("JWT Service initialized with expiration time: {} minutes", expirationTime);
	}

	public String createToken(Long id, String email, UserRole role) {
		ZonedDateTime now = ZonedDateTime.now();
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey);
			String token = JWT.create()
				.withIssuer("auth0")
				.withSubject(id.toString())
				.withClaim("email", email)
				.withClaim("roles", List.of(role.getRole()))
				.withIssuedAt(now.toInstant())
				.withExpiresAt(now.plusMinutes(expirationTime).toInstant())
				.sign(algorithm);
			logger.debug("Created JWT token for user ID: {}, email: {}, role: {}", id, email, role);
			return token;
		} catch (JWTCreationException e) {
			logger.error("Failed to create JWT token: {}", e.getMessage());
			throw new JWTCreationException("JWT 생성 실패", e);
		}
	}

	public DecodedJWT verifyToken(String token) {
		try {
			Algorithm algorithm = Algorithm.HMAC256(secretKey);
			JWTVerifier verifier = JWT.require(algorithm)
				.withIssuer("auth0")
				.build();
			DecodedJWT decodedJWT = verifier.verify(token);
			logger.debug("Verified JWT token, subject: {}, roles: {}",
				decodedJWT.getSubject(), decodedJWT.getClaim("roles").asList(String.class));
			return decodedJWT;
		} catch (JWTVerificationException e) {
			logger.error("JWT verification failed: {}", e.getMessage());
			throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN); // 예외 메시지 포함
		}
	}
}