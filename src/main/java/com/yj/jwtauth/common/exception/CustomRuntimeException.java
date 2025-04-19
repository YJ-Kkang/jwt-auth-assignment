package com.yj.jwtauth.common.exception;

import com.yj.jwtauth.common.exception.error.ErrorCode;
import lombok.Getter;

@Getter
public class CustomRuntimeException extends RuntimeException {

	private final ErrorCode errorCode;

	public CustomRuntimeException(ErrorCode errorCode) {
		super(errorCode.getMessage()); // 부모 클래스에 메시지 전달
		this.errorCode = errorCode;
	}
}