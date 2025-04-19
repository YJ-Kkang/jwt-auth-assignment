package com.yj.jwtauth.domain.user.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
	ADMIN("ROLE_ADMIN", "관리자"),
	USER("ROLE_USER", "일반 회원");

	private final String role;
	private final String description;
}