package com.yj.jwtauth.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SigninResponseDto {

	@Schema(description = "JWT 토큰", example = "eyJh7... | SwaggerConfig가 Bearer를 붙여줘서 최종적으로 `Authorization` 헤더에 `Bearer ` 접두사가 붙음.")
	private final String token;

	public SigninResponseDto(String token) {
		this.token = token;
	}
}
