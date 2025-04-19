package com.yj.jwtauth.common.exception;

import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.common.exception.error.ErrorResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(CustomRuntimeException.class)
	protected ResponseEntity<ErrorResponseDto> handleCustomException(CustomRuntimeException e) {
		return ResponseEntity
			.status(e.getErrorCode().getStatus())
			.body(new ErrorResponseDto(e.getErrorCode()));
	}

	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<ErrorResponseDto> handleAccessDeniedException(AccessDeniedException e) {
		return ResponseEntity
			.status(ErrorCode.ACCESS_DENIED.getStatus())
			.body(new ErrorResponseDto(ErrorCode.ACCESS_DENIED));
	}
}