package com.yj.jwtauth.domain.user.dto.response;

import com.yj.jwtauth.common.entity.User;

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

	@Schema(description = "역할 목록", example = "ADMIN 또는 USER")
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

	/**
	 * 단일 권한을 편하게 꺼내 쓰는 Getter.
	 * Jackson이 이 메서드를 JSON 프로퍼티 'role'로 직렬화 함
	 */
	@Schema(description = "단일 권한", example = "ADMIN")
	public String getRole() {
		// roles 리스트에서 첫 번째 요소를 꺼냄
		return (roles != null && !roles.isEmpty()) ? roles.get(0) : null;
	}
}