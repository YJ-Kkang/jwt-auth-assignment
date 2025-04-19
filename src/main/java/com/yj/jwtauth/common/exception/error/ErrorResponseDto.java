package com.yj.jwtauth.common.exception.error;

import lombok.Getter;

@Getter
public class ErrorResponseDto {

	private final ErrorDetail error;

	// ErrorCode만으로 생성하게 제한
	public ErrorResponseDto(ErrorCode errorCode) {
		this.error = new ErrorDetail(errorCode.name(), errorCode.getMessage());
	}

	@Getter
	public static class ErrorDetail {
		private final String code;
		private final String message;

		public ErrorDetail(String code, String message) {
			this.code = code;
			this.message = message;
		}
	}
}