package com.yj.jwtauth.common.datas;

import com.yj.jwtauth.common.entity.User;
import com.yj.jwtauth.domain.user.dto.request.SigninRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SignupRequestDto;
import com.yj.jwtauth.domain.user.dto.request.RoleUpdateRequestDto;
import com.yj.jwtauth.domain.user.enums.UserRole;
import org.springframework.test.util.ReflectionTestUtils;

/**
 * 테스트 전용 사용자 데이터 정리 클래스
 */
public class SharedData {

	// === 유효한 사용자 정보 ===
	public static final Long VALID_USER_ID = 1L;
	public static final String VALID_USERNAME = "사용자A";
	public static final String VALID_EMAIL = "userA@example.com";
	public static final String VALID_PASSWORD = "Password123!";

	// 회원가입 요청 DTO 생성
	public static SignupRequestDto createValidSignupDto() {
		return new SignupRequestDto(VALID_EMAIL, VALID_USERNAME, VALID_PASSWORD);
	}

	// 로그인 요청 DTO 생성
	public static SigninRequestDto createValidSigninDto() {
		return new SigninRequestDto(VALID_EMAIL, VALID_PASSWORD);
	}

	// 유효한 사용자 엔티티 생성 + id, role, isDeleted 세팅
	public static User createValidUserEntity() {
		User user = User.builder()
			.username(VALID_USERNAME)
			.email(VALID_EMAIL)
			.password(VALID_PASSWORD)
			.build();
		// 강제로 private 필드 값 설정
		ReflectionTestUtils.setField(user, "id", VALID_USER_ID);
		ReflectionTestUtils.setField(user, "role", UserRole.USER);
		ReflectionTestUtils.setField(user, "isDeleted", false);
		return user;
	}

	// === 관리자 정보 ===
	public static final Long ADMIN_USER_ID = 2L;
	public static final String ADMIN_USERNAME = "관리자B";
	public static final String ADMIN_EMAIL = "adminB@example.com";
	public static final String ADMIN_PASSWORD = "Admin456!";

	public static SignupRequestDto createAdminSignupDto() {
		return new SignupRequestDto(ADMIN_EMAIL, ADMIN_USERNAME, ADMIN_PASSWORD);
	}

	public static User createAdminUserEntity() {
		User admin = User.builder()
			.username(ADMIN_USERNAME)
			.email(ADMIN_EMAIL)
			.password(ADMIN_PASSWORD)
			.build();
		// admin role 및 기타 필드 설정
		ReflectionTestUtils.setField(admin, "id", ADMIN_USER_ID);
		ReflectionTestUtils.setField(admin, "role", UserRole.ADMIN);
		ReflectionTestUtils.setField(admin, "isDeleted", false);
		return admin;
	}

	// === 기타 테스트용 DTO ===

	// 중복 이메일 회원가입 요청 DTO (VALID_EMAIL 재사용)
	public static SignupRequestDto createDuplicateEmailSignupDto() {
		return new SignupRequestDto(VALID_EMAIL, "다른이름", "DiffPassword1!");
	}

	// 잘못된 비밀번호 로그인 요청 DTO
	public static SigninRequestDto createInvalidPasswordSigninDto() {
		return new SigninRequestDto(VALID_EMAIL, "WrongPassword123!");
	}

	// 존재하지 않는 이메일로 로그인 요청 DTO
	public static SigninRequestDto createNonexistentUserSigninDto() {
		return new SigninRequestDto("notfound@example.com", "NoUserPassword1!");
	}

	// 관리자 권한 부여 요청 DTO
	public static RoleUpdateRequestDto createAssignAdminRoleDto() {
		return new RoleUpdateRequestDto(UserRole.ADMIN);
	}
}