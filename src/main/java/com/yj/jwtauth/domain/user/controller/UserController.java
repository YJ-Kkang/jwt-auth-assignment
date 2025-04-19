package com.yj.jwtauth.domain.user.controller;

import com.yj.jwtauth.common.exception.CustomRuntimeException;
import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.domain.user.dto.request.RoleUpdateRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SigninRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SignupRequestDto;
import com.yj.jwtauth.domain.user.dto.response.SigninResponseDto;
import com.yj.jwtauth.domain.user.dto.response.SignupResponseDto;
import com.yj.jwtauth.domain.user.dto.response.UserResponseDto;
import com.yj.jwtauth.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);
	private final UserService userService;

	// 회원가입: 유저(사용자)
	@PostMapping("/users/signup")
	@Operation(
		summary = "일반 사용자 회원가입",
		description = "일반 사용자 계정을 생성합니다."
	)
	public ResponseEntity<SignupResponseDto> userSignupAPI(
		@Valid @RequestBody SignupRequestDto requestDto
	) {
		logger.debug("Processing user signup: {}", requestDto.getEmail());
		SignupResponseDto response = userService.userSignup(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 회원가입: 어드민(관리자)
	@PostMapping("/admins/signup")
	@Operation(
		summary = "관리자 회원가입",
		description = "관리자 계정을 생성합니다."
	)
	public ResponseEntity<SignupResponseDto> adminSignupAPI(
		@Valid @RequestBody SignupRequestDto requestDto
	) {
		logger.debug("Processing admin signup: {}", requestDto.getEmail());
		SignupResponseDto response = userService.adminSignup(requestDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	// 로그인
	@PostMapping("/signin")
	@Operation(
		summary = "로그인",
		description = "이메일과 비밀번호로 로그인합니다."
	)
	public ResponseEntity<SigninResponseDto> signinAPI(
		@Valid @RequestBody SigninRequestDto requestDto
	) {
		logger.debug("Processing signin: {}", requestDto.getEmail());
		SigninResponseDto response = userService.userOrAdminSignin(requestDto);
		return ResponseEntity.ok(response);
	}

	// 내 정보 조회
	@GetMapping("/my-informations")
	@Operation(
		summary = "내 정보 조회",
		description = "현재 로그인한 유저의 정보를 조회합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)	public ResponseEntity<UserResponseDto> getMyInfoAPI(HttpServletRequest request) {
		Long userId = (Long) request.getAttribute("userId");
		logger.debug("Retrieving info for userId: {}", userId);
		if (userId == null) {
			logger.error("userId attribute is null in request");
			throw new CustomRuntimeException(ErrorCode.INVALID_TOKEN);
		}
		UserResponseDto response = userService.getMyInfo(userId);
		return ResponseEntity.ok(response);
	}

	// 관리자 권한 부여
	@PatchMapping("/admins/users/{userId}/roles")
	@Operation(
		summary = "관리자 권한 부여",
		description = "특정 유저에게 ADMIN 권한을 부여합니다.",
		security = @SecurityRequirement(name = "bearerAuth")
	)
	public ResponseEntity<UserResponseDto> assignAdminRoleAPI(
		@PathVariable Long userId,
		@Valid @RequestBody RoleUpdateRequestDto requestDto
	) {
		logger.debug("Assigning admin role to userId: {}", userId);
		UserResponseDto response = userService.assignAdminRole(userId, requestDto);
		return ResponseEntity.ok(response);
	}
}