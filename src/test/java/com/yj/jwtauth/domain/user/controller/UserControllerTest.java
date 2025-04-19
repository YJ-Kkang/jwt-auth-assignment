package com.yj.jwtauth.domain.user.controller;

import com.yj.jwtauth.common.datas.SharedData;
import com.yj.jwtauth.common.entity.User;
import com.yj.jwtauth.common.exception.CustomRuntimeException;
import com.yj.jwtauth.common.exception.error.ErrorCode;
import com.yj.jwtauth.domain.user.dto.request.RoleUpdateRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SigninRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SignupRequestDto;
import com.yj.jwtauth.domain.user.dto.response.SigninResponseDto;
import com.yj.jwtauth.domain.user.dto.response.SignupResponseDto;
import com.yj.jwtauth.domain.user.dto.response.UserResponseDto;
import com.yj.jwtauth.domain.user.enums.UserRole;
import com.yj.jwtauth.domain.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

	// === 가짜 객체(Mock) ===
	@Mock
	private UserService userService;

	@Mock
	private HttpServletRequest request;

	// === 테스트 대상 클래스(실제 객체) ===
	@InjectMocks
	private UserController userController;

	// UserResponseDto가 abstract이므로 테스트용 구체 클래스 정의
	private static class TestUserResponseDto extends UserResponseDto {
		public TestUserResponseDto(User user) {
			super(user);
		}
	}

	// User 객체의 필드를 초기화하는 헬퍼 메서드
	private User initializeUser(User user) {
		ReflectionTestUtils.setField(user, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(user, "updatedAt", LocalDateTime.now());
		return user;
	}

	/*
	 * userSignupAPI
	 */
	@Test
	@DisplayName("success: 사용자 회원가입 API")
	void userSignupAPI() {
		// given
		SignupRequestDto requestDto = SharedData.createValidSignupDto();
		LocalDateTime now = LocalDateTime.now();
		SignupResponseDto responseDto = SignupResponseDto.builder()
			.id(1L)
			.username(SharedData.VALID_USERNAME)
			.email(SharedData.VALID_EMAIL)
			.role(UserRole.USER)
			.createdAt(now)
			.updatedAt(now)
			.build();

		when(userService.userSignup(requestDto)).thenReturn(responseDto);

		// when
		ResponseEntity<SignupResponseDto> response = userController.userSignupAPI(requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(responseDto);
		assertThat(response.getBody().getEmail()).isEqualTo(SharedData.VALID_EMAIL);
		assertThat(response.getBody().getUsername()).isEqualTo(SharedData.VALID_USERNAME);
		assertThat(response.getBody().getRole()).isEqualTo(UserRole.USER.name());

		verify(userService, times(1)).userSignup(requestDto);
	}

	@Test
	@DisplayName("fail: 중복 이메일로 사용자 회원가입 시 예외 발생")
	void userSignupAPIDuplicateEmail() {
		// given
		SignupRequestDto requestDto = SharedData.createDuplicateEmailSignupDto();

		when(userService.userSignup(requestDto)).thenThrow(new CustomRuntimeException(ErrorCode.EMAIL_ALREADY_EXISTS));

		// when, then
		assertThatThrownBy(() -> userController.userSignupAPI(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);

		verify(userService, times(1)).userSignup(requestDto);
	}

	/*
	 * adminSignupAPI
	 */
	@Test
	@DisplayName("success: 관리자 회원가입 API")
	void adminSignupAPI() {
		// given
		SignupRequestDto requestDto = SharedData.createAdminSignupDto();
		LocalDateTime now = LocalDateTime.now();
		SignupResponseDto responseDto = SignupResponseDto.builder()
			.id(2L)
			.username(SharedData.ADMIN_USERNAME)
			.email(SharedData.ADMIN_EMAIL)
			.role(UserRole.ADMIN)
			.createdAt(now)
			.updatedAt(now)
			.build();

		when(userService.adminSignup(requestDto)).thenReturn(responseDto);

		// when
		ResponseEntity<SignupResponseDto> response = userController.adminSignupAPI(requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(response.getBody()).isEqualTo(responseDto);
		assertThat(response.getBody().getEmail()).isEqualTo(SharedData.ADMIN_EMAIL);
		assertThat(response.getBody().getUsername()).isEqualTo(SharedData.ADMIN_USERNAME);
		assertThat(response.getBody().getRole()).isEqualTo(UserRole.ADMIN.name());

		verify(userService, times(1)).adminSignup(requestDto);
	}

	@Test
	@DisplayName("fail: 중복 이메일로 관리자 회원가입 시 예외 발생")
	void adminSignupAPIDuplicateEmail() {
		// given
		SignupRequestDto requestDto = SharedData.createDuplicateEmailSignupDto();

		when(userService.adminSignup(requestDto)).thenThrow(new CustomRuntimeException(ErrorCode.EMAIL_ALREADY_EXISTS));

		// when, then
		assertThatThrownBy(() -> userController.adminSignupAPI(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);

		verify(userService, times(1)).adminSignup(requestDto);
	}

	/*
	 * signinAPI
	 */
	@Test
	@DisplayName("success: 로그인 API")
	void signinAPI() {
		// given
		SigninRequestDto requestDto = SharedData.createValidSigninDto();
		SigninResponseDto responseDto = new SigninResponseDto("jwt-token");

		when(userService.userOrAdminSignin(requestDto)).thenReturn(responseDto);

		// when
		ResponseEntity<SigninResponseDto> response = userController.signinAPI(requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(responseDto);
		assertThat(response.getBody().getToken()).isEqualTo("jwt-token");

		verify(userService, times(1)).userOrAdminSignin(requestDto);
	}

	@Test
	@DisplayName("fail: 존재하지 않는 이메일로 로그인 시 예외 발생")
	void signinAPIFailByEmail() {
		// given
		SigninRequestDto requestDto = SharedData.createNonexistentUserSigninDto();

		when(userService.userOrAdminSignin(requestDto)).thenThrow(new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS));

		// when, then
		assertThatThrownBy(() -> userController.signinAPI(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

		verify(userService, times(1)).userOrAdminSignin(requestDto);
	}

	@Test
	@DisplayName("fail: 잘못된 비밀번호로 로그인 시 예외 발생")
	void signinAPIFailByPassword() {
		// given
		SigninRequestDto requestDto = SharedData.createInvalidPasswordSigninDto();

		when(userService.userOrAdminSignin(requestDto)).thenThrow(new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS));

		// when, then
		assertThatThrownBy(() -> userController.signinAPI(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

		verify(userService, times(1)).userOrAdminSignin(requestDto);
	}

	/*
	 * getMyInfoAPI
	 */
	@Test
	@DisplayName("success: 내 정보 조회 API")
	void getMyInfoAPI() {
		// given
		Long userId = SharedData.VALID_USER_ID;
		User user = initializeUser(SharedData.createValidUserEntity());
		UserResponseDto responseDto = new TestUserResponseDto(user);

		when(request.getAttribute("userId")).thenReturn(userId);
		when(userService.getMyInfo(userId)).thenReturn(responseDto);

		// when
		ResponseEntity<UserResponseDto> response = userController.getMyInfoAPI(request);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(responseDto);
		assertThat(response.getBody().getId()).isEqualTo(userId);
		assertThat(response.getBody().getEmail()).isEqualTo(SharedData.VALID_EMAIL);
		assertThat(response.getBody().getRoles()).containsExactly("USER");

		verify(request, times(1)).getAttribute("userId");
		verify(userService, times(1)).getMyInfo(userId);
	}

	@Test
	@DisplayName("fail: 유효하지 않은 토큰으로 내 정보 조회 시 예외 발생")
	void getMyInfoAPIFailInvalidToken() {
		// given
		when(request.getAttribute("userId")).thenReturn(null);

		// when, then
		assertThatThrownBy(() -> userController.getMyInfoAPI(request))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_TOKEN);

		verify(request, times(1)).getAttribute("userId");
		verify(userService, never()).getMyInfo(anyLong());
	}

	@Test
	@DisplayName("fail: 존재하지 않는 유저 조회 시 예외 발생")
	void getMyInfoAPIFailUserNotFound() {
		// given
		Long userId = 999L;

		when(request.getAttribute("userId")).thenReturn(userId);
		when(userService.getMyInfo(userId)).thenThrow(new CustomRuntimeException(ErrorCode.ACCESS_DENIED));

		// when, then
		assertThatThrownBy(() -> userController.getMyInfoAPI(request))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

		verify(request, times(1)).getAttribute("userId");
		verify(userService, times(1)).getMyInfo(userId);
	}

	/*
	 * assignAdminRoleAPI
	 */
	@Test
	@DisplayName("success: 관리자 권한 부여 API")
	void assignAdminRoleAPI() {
		// given
		Long userId = SharedData.VALID_USER_ID;
		RoleUpdateRequestDto requestDto = SharedData.createAssignAdminRoleDto();
		User user = initializeUser(SharedData.createValidUserEntity());

		// ReflectionTestUtils를 사용하여 user의 role 필드를 강제로 ADMIN으로 변경
		ReflectionTestUtils.setField(user, "role", UserRole.ADMIN);

		UserResponseDto responseDto = new TestUserResponseDto(user);

		when(userService.assignAdminRole(userId, requestDto)).thenReturn(responseDto);

		// when
		ResponseEntity<UserResponseDto> response = userController.assignAdminRoleAPI(userId, requestDto);

		// then
		assertThat(response).isNotNull();
		assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(response.getBody()).isEqualTo(responseDto);
		assertThat(response.getBody().getId()).isEqualTo(userId);
		assertThat(response.getBody().getRoles()).containsExactly("ADMIN");

		verify(userService, times(1)).assignAdminRole(userId, requestDto);
	}

	@Test
	@DisplayName("fail: 존재하지 않는 유저에게 권한 부여 시 예외 발생")
	void assignAdminRoleAPIFail() {
		// given
		Long userId = 999L;
		RoleUpdateRequestDto requestDto = SharedData.createAssignAdminRoleDto();

		when(userService.assignAdminRole(userId, requestDto)).thenThrow(new CustomRuntimeException(ErrorCode.ACCESS_DENIED));

		// when, then
		assertThatThrownBy(() -> userController.assignAdminRoleAPI(userId, requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

		verify(userService, times(1)).assignAdminRole(userId, requestDto);
	}
}