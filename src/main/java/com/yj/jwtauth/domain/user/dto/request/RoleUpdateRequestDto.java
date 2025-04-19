package com.yj.jwtauth.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.yj.jwtauth.domain.user.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class RoleUpdateRequestDto {

	@Schema(description = "부여할 역할", example = "ADMIN")
	@NotNull(message = "역할은 필수입니다.")
	private final UserRole role;

	@JsonCreator
	public RoleUpdateRequestDto(@JsonProperty("role") UserRole role) {
		this.role = role;
	}
}