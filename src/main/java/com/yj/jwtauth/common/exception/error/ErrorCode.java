package com.yj.jwtauth.common.exception.error;

import org.springframework.http.HttpStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

	// 회원가입 - 이메일 중복
	EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),

	// 로그인 실패
	INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "아이디 또는 비밀번호가 올바르지 않습니다."),

	// 인가 실패 (관리자 권한 없음)
	ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),

	// 유효하지 않은 토큰, 토큰 만료
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 인증 토큰입니다.");

	private final HttpStatus status;
	private final String message;
}
