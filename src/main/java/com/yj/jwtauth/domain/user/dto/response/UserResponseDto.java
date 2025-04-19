package com.yj.jwtauth.domain.user.dto.response;

import com.yj.jwtauth.common.entity.User;
import com.yj.jwtauth.domain.user.enums.UserRole;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public abstract class UserResponseDto {

	@Schema(description = "사용자 또는 관리자 ID", example = "1")
	private final Long id;

	@Schema(description = "사용자 또는 관리자 이름", example = "이름")
	private final String username;

	@Schema(description = "가입한 이메일", example = "user@example.com")
	private final String email;

	@Schema(description = "역할", example = "ADMIN 또는 USER")
	private final List<String> roles;

	@Schema(description = "탈퇴 여부", example = "false")
	private final boolean isDeleted;

	@Schema(description = "생성 일시", example = "2025-04-19T11:00:00")
	private final LocalDateTime createdAt;

	@Schema(description = "수정 일시", example = "2025-04-19T11:00:00")
	private final LocalDateTime updatedAt;

	protected UserResponseDto(User user) {
		this.id = user.getId();
		this.username = user.getUsername();
		this.email = user.getEmail();
		this.roles = List.of(user.getRole().name());
		this.isDeleted = user.isDeleted();
		this.createdAt = user.getCreatedAt();
		this.updatedAt = user.getUpdatedAt();
	}
}