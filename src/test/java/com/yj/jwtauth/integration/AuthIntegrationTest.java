package com.yj.jwtauth.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yj.jwtauth.common.entity.User;
import com.yj.jwtauth.domain.user.dto.request.RoleUpdateRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SigninRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SignupRequestDto;
import com.yj.jwtauth.domain.user.enums.UserRole;
import com.yj.jwtauth.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
// 통합 테스트
class AuthIntegrationTest {

	@Autowired private MockMvc mockMvc;
	@Autowired private ObjectMapper objectMapper;
	@Autowired private UserRepository userRepository;
	@Autowired private PasswordEncoder passwordEncoder;

	private Long userId;

	@BeforeEach
	void setUp() {
		userRepository.deleteAll();

		User user = User.builder()
			.email("user@example.com")
			.password(passwordEncoder.encode("Password123!"))
			.username("사용자")
			.build();

		userRepository.save(user);
		userId = user.getId();
	}

	@Test
	@DisplayName("success: 사용자 회원가입 성공")
	void signupUser_success() throws Exception {
		SignupRequestDto dto = new SignupRequestDto("new@example.com", "새사용자", "NewPassword1!");

		mockMvc.perform(post("/api/users/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.email").value("new@example.com"))
			.andExpect(jsonPath("$.username").value("새사용자"))
			.andExpect(jsonPath("$.role").value("USER"));
	}

	@Test
	@DisplayName("fail: 중복 이메일 회원가입 시 예외 발생")
	void signupUser_fail_duplicateEmail() throws Exception {
		SignupRequestDto dto = new SignupRequestDto("user@example.com", "다른이름", "Password123!");

		mockMvc.perform(post("/api/users/signup")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isConflict());
	}

	@Test
	@DisplayName("success: 로그인 성공 시 JWT 반환")
	void signinReturnsJwtToken() throws Exception {
		SigninRequestDto request = new SigninRequestDto("user@example.com", "Password123!");

		mockMvc.perform(post("/api/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").exists());
	}

	@Test
	@DisplayName("fail: 존재하지 않는 이메일로 로그인 시 실패")
	void signinFail_wrongEmail() throws Exception {
		SigninRequestDto dto = new SigninRequestDto("noexist@example.com", "Password123!");

		mockMvc.perform(post("/api/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("fail: 틀린 비밀번호로 로그인 시 실패")
	void signinFail_wrongPassword() throws Exception {
		SigninRequestDto dto = new SigninRequestDto("user@example.com", "WrongPassword!");

		mockMvc.perform(post("/api/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("fail: JWT 없이 보호 API 접근 시 401")
	void accessProtectedApiWithoutJwt() throws Exception {
		mockMvc.perform(get("/api/my-informations"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("success: JWT 포함 보호 API 접근 성공")
	void accessProtectedApiWithJwt() throws Exception {
		String token = loginAndGetToken("user@example.com", "Password123!");

		mockMvc.perform(get("/api/my-informations")
				.header("Authorization", token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("user@example.com"));
	}

	@Test
	@DisplayName("success: 관리자 JWT로 권한 부여 성공")
	void assignAdminRoleWithAdminJwt() throws Exception {
		User admin = User.builder()
			.email("admin@example.com")
			.password(passwordEncoder.encode("Admin123!"))
			.username("관리자")
			.build();
		admin.updateRole(UserRole.ADMIN);
		userRepository.save(admin);

		String adminToken = loginAndGetToken("admin@example.com", "Admin123!");
		RoleUpdateRequestDto dto = new RoleUpdateRequestDto(UserRole.ADMIN);

		mockMvc.perform(patch("/api/admins/users/" + userId + "/roles")
				.header("Authorization", adminToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.role").value("ADMIN"));
	}

	@Test
	@DisplayName("fail: JWT 없이 관리자 권한 부여 시 401")
	void assignAdminRoleWithoutJwt() throws Exception {
		RoleUpdateRequestDto dto = new RoleUpdateRequestDto(UserRole.ADMIN);

		mockMvc.perform(patch("/api/admins/users/" + userId + "/roles")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isUnauthorized());
	}

	@Test
	@DisplayName("fail: 일반 유저 JWT로 권한 부여 시 403")
	void assignAdminRoleWithUserJwt() throws Exception {
		String userToken = loginAndGetToken("user@example.com", "Password123!");
		RoleUpdateRequestDto dto = new RoleUpdateRequestDto(UserRole.ADMIN);

		mockMvc.perform(patch("/api/admins/users/" + userId + "/roles")
				.header("Authorization", userToken)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("fail: 존재하지 않는 유저에게 권한 부여 시 403")
	void assignAdminRoleToNonexistentUser() throws Exception {
		User admin = User.builder()
			.email("admin2@example.com")
			.password(passwordEncoder.encode("Admin123!"))
			.username("관리자2")
			.build();
		admin.updateRole(UserRole.ADMIN);
		userRepository.save(admin);

		String token = loginAndGetToken("admin2@example.com", "Admin123!");
		RoleUpdateRequestDto dto = new RoleUpdateRequestDto(UserRole.ADMIN);

		mockMvc.perform(patch("/api/admins/users/99999/roles")
				.header("Authorization", token)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isForbidden());
	}

	// 로그인 후 JWT 반환 유틸
	private String loginAndGetToken(String email, String password) throws Exception {
		SigninRequestDto dto = new SigninRequestDto(email, password);
		MvcResult result = mockMvc.perform(post("/api/signin")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dto)))
			.andExpect(status().isOk())
			.andReturn();

		String token = objectMapper.readTree(result.getResponse().getContentAsString())
			.get("token").asText();
		return "Bearer " + token; // "Bearer " 접두사 추가
	}
}