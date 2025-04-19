package com.yj.jwtauth.domain.user.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SigninResponseDto {

	@Schema(description = "JWT 토큰", example = "Bearer eyJh7...")
	private String token;

	public SigninResponseDto(String token) {
		this.token = token;
	}
}
