package com.yj.jwtauth.domain.user.service;

import com.yj.jwtauth.auth.JwtService;
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
import com.yj.jwtauth.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

	// === 가짜 객체(Mock) ===
	@Mock
	private UserRepository userRepository;

	@Mock
	private PasswordEncoder passwordEncoder;

	@Mock
	private JwtService jwtService;

	// === 테스트 대상 클래스(실제 객체) ===
	@InjectMocks
	private UserServiceImpl userService;

	/*
	 * userSignup
	 */
	@Test
	@DisplayName("success: 사용자 회원가입")
	void userSignup() {
		// given
		SignupRequestDto requestDto = SharedData.createValidSignupDto();
		User user = SharedData.createValidUserEntity();

		when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(user);

		// when
		SignupResponseDto result = userService.userSignup(requestDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(user.getEmail());
		assertThat(result.getUsername()).isEqualTo(user.getUsername());
		assertThat(UserRole.valueOf(result.getRole())).isEqualTo(UserRole.USER);

		verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
		verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("fail: 중복 이메일로 회원가입 시 예외 발생")
	void userSignupDuplicate() {
		// given
		SignupRequestDto requestDto = SharedData.createDuplicateEmailSignupDto();

		when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

		// when, then
		assertThatThrownBy(() -> userService.userSignup(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);

		verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
		verify(userRepository, never()).save(any());
	}

	/*
	 * adminSignup
	 */
	@Test
	@DisplayName("success: 관리자 회원가입")
	void adminSignup() {
		// given
		SignupRequestDto requestDto = SharedData.createAdminSignupDto();
		User admin = SharedData.createAdminUserEntity();

		when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(false);
		when(passwordEncoder.encode(requestDto.getPassword())).thenReturn("encodedPassword");
		when(userRepository.save(any(User.class))).thenReturn(admin);

		// when
		SignupResponseDto result = userService.adminSignup(requestDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getEmail()).isEqualTo(admin.getEmail());
		assertThat(result.getUsername()).isEqualTo(admin.getUsername());
		assertThat(UserRole.valueOf(result.getRole())).isEqualTo(UserRole.ADMIN);

		verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
		verify(passwordEncoder, times(1)).encode(requestDto.getPassword());
		verify(userRepository, times(1)).save(any(User.class));
	}

	@Test
	@DisplayName("fail: 중복 이메일로 관리자 회원가입 시 예외 발생")
	void adminSignupDuplicate() {
		// given
		SignupRequestDto requestDto = SharedData.createDuplicateEmailSignupDto();

		when(userRepository.existsByEmail(requestDto.getEmail())).thenReturn(true);

		// when, then
		assertThatThrownBy(() -> userService.adminSignup(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.EMAIL_ALREADY_EXISTS);

		verify(userRepository, times(1)).existsByEmail(requestDto.getEmail());
		verify(userRepository, never()).save(any());
	}

	/*
	 * userOrAdminSignin
	 */
	@Test
	@DisplayName("success: 로그인 성공")
	void userOrAdminSignin() {
		// given
		SigninRequestDto requestDto = SharedData.createValidSigninDto();
		User user = SharedData.createValidUserEntity();

		when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(true);
		when(jwtService.createToken(user.getId(), user.getEmail(), user.getRole())).thenReturn("jwt-token");

		// when
		SigninResponseDto result = userService.userOrAdminSignin(requestDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getToken()).isEqualTo("jwt-token"); // "Bearer " 접두사 제거

		verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
		verify(passwordEncoder, times(1)).matches(requestDto.getPassword(), user.getPassword());
		verify(jwtService, times(1)).createToken(user.getId(), user.getEmail(), user.getRole());
	}

	@Test
	@DisplayName("fail: 존재하지 않는 이메일로 로그인 시 예외 발생")
	void signinFailByEmail() {
		// given
		SigninRequestDto requestDto = SharedData.createNonexistentUserSigninDto();

		when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> userService.userOrAdminSignin(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

		verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
	}

	@Test
	@DisplayName("fail: 잘못된 비밀번호로 로그인 시 예외 발생")
	void signinFailByPassword() {
		// given
		SigninRequestDto requestDto = SharedData.createInvalidPasswordSigninDto();
		User user = SharedData.createValidUserEntity();

		when(userRepository.findByEmail(requestDto.getEmail())).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(requestDto.getPassword(), user.getPassword())).thenReturn(false);

		// when, then
		assertThatThrownBy(() -> userService.userOrAdminSignin(requestDto))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_CREDENTIALS);

		verify(userRepository, times(1)).findByEmail(requestDto.getEmail());
		verify(passwordEncoder, times(1)).matches(requestDto.getPassword(), user.getPassword());
	}

	/*
	 * assignAdminRole
	 */
	@Test
	@DisplayName("success: 관리자 권한 부여")
	void assignAdminRole() {
		// given
		Long userId = SharedData.VALID_USER_ID;
		User user = SharedData.createValidUserEntity();
		RoleUpdateRequestDto requestDto = SharedData.createAssignAdminRoleDto();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		UserResponseDto result = userService.assignAdminRole(userId, requestDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getRoles()).containsExactly("ADMIN");

		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("fail: 존재하지 않는 유저에게 권한 부여 시 예외 발생")
	void assignAdminRoleFail() {
		// given
		Long userId = 999L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> userService.assignAdminRole(userId, SharedData.createAssignAdminRoleDto()))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

		verify(userRepository, times(1)).findById(userId);
	}

	/*
	 * getMyInfo
	 */
	@Test
	@DisplayName("success: 내 정보 조회")
	void getMyInfo() {
		// given
		Long userId = SharedData.VALID_USER_ID;
		User user = SharedData.createValidUserEntity();

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		// when
		UserResponseDto result = userService.getMyInfo(userId);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(userId);
		assertThat(result.getEmail()).isEqualTo(user.getEmail());
		assertThat(result.getRoles()).containsExactly("USER");

		verify(userRepository, times(1)).findById(userId);
	}

	@Test
	@DisplayName("fail: 존재하지 않는 유저 조회 시 예외 발생")
	void getMyInfoFail() {
		// given
		Long userId = 999L;

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		// when, then
		assertThatThrownBy(() -> userService.getMyInfo(userId))
			.isInstanceOf(CustomRuntimeException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);

		verify(userRepository, times(1)).findById(userId);
	}
}