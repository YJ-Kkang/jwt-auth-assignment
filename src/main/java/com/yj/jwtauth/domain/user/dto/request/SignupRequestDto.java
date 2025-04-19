package com.yj.jwtauth.domain.user.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class SignupRequestDto {

	@Schema(description = "사용자 또는 관리자 이메일", example = "user@example.com")
	@NotBlank(message = "이메일은 필수입니다.")
	@Email(message = "유효한 이메일 형식이 아닙니다.")
	private final String email;

	@Schema(description = "사용자 또는 관리자 이름", example = "이름")
	@NotBlank(message = "이름은 필수입니다.")
	private final String username;

	@Schema(description = "비밀번호", example = "Password123!")
	@NotBlank(message = "비밀번호는 필수입니다.")
	private final String password;

	public SignupRequestDto(String email, String username, String password) {
		this.email = email;
		this.username = username;
		this.password = password;
	}
}