package com.yj.jwtauth.common.entity;

import com.yj.jwtauth.common.datas.SharedData;
import com.yj.jwtauth.domain.user.enums.UserRole;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class UserEntityTest {

	@Autowired
	private TestEntityManager entityManager;

	// SharedData에서 생성한 User 객체의 ID를 제거하는 헬퍼 메서드
	private User prepareUserForPersist(User user) {
		// ID를 null로 설정하여 새로운 엔티티로 처리되도록 함
		ReflectionTestUtils.setField(user, "id", null);
		return user;
	}

	/*
	 * saveUser
	 */
	@Test
	@DisplayName("success: 유저 엔티티 저장 성공")
	void saveUser() {
		// given
		User user = prepareUserForPersist(SharedData.createValidUserEntity());

		// when
		entityManager.persist(user);
		entityManager.flush();

		// then
		assertThat(user.getId()).isNotNull(); // ID가 자동 생성됨
		assertThat(user.getUsername()).isEqualTo(SharedData.VALID_USERNAME);
		assertThat(user.getEmail()).isEqualTo(SharedData.VALID_EMAIL);
		assertThat(user.getPassword()).isEqualTo(SharedData.VALID_PASSWORD);
		assertThat(user.getRole()).isEqualTo(UserRole.USER); // 기본값 확인
		assertThat(user.isDeleted()).isFalse(); // 기본값 확인
		assertThat(user.getCreatedAt()).isNotNull(); // BaseEntity에서 상속
		assertThat(user.getUpdatedAt()).isNotNull(); // BaseEntity에서 상속
	}

	/*
	 * saveUserWithAdminRole
	 */
	@Test
	@DisplayName("success: 관리자 역할로 유저 엔티티 저장 성공")
	void saveUserWithAdminRole() {
		// given
		User admin = prepareUserForPersist(SharedData.createAdminUserEntity());

		// when
		entityManager.persist(admin);
		entityManager.flush();

		// then
		assertThat(admin.getId()).isNotNull();
		assertThat(admin.getUsername()).isEqualTo(SharedData.ADMIN_USERNAME);
		assertThat(admin.getEmail()).isEqualTo(SharedData.ADMIN_EMAIL);
		assertThat(admin.getPassword()).isEqualTo(SharedData.ADMIN_PASSWORD);
		assertThat(admin.getRole()).isEqualTo(UserRole.ADMIN);
		assertThat(admin.isDeleted()).isFalse();
	}

	/*
	 * updateRole
	 */
	@Test
	@DisplayName("success: 유저 역할 변경 성공")
	void updateRole() {
		// given
		User user = prepareUserForPersist(SharedData.createValidUserEntity());
		entityManager.persist(user);
		entityManager.flush();

		// when
		user.updateRole(UserRole.ADMIN);
		entityManager.flush();

		// then
		User updatedUser = entityManager.find(User.class, user.getId());
		assertThat(updatedUser.getRole()).isEqualTo(UserRole.ADMIN);
	}

	/*
	 * failOnNullUsername
	 */
	@Test
	@DisplayName("fail: username이 null일 때 저장 실패")
	void failOnNullUsername() {
		// given
		User user = User.builder()
			.username(null) // nullable = false 제약 조건 위반
			.email(SharedData.VALID_EMAIL)
			.password(SharedData.VALID_PASSWORD)
			.build();

		// when, then
		assertThrows(PersistenceException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}

	/*
	 * failOnNullEmail
	 */
	@Test
	@DisplayName("fail: email이 null일 때 저장 실패")
	void failOnNullEmail() {
		// given
		User user = User.builder()
			.username(SharedData.VALID_USERNAME)
			.email(null) // nullable = false 제약 조건 위반
			.password(SharedData.VALID_PASSWORD)
			.build();

		// when, then
		assertThrows(PersistenceException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}

	/*
	 * failOnNullPassword
	 */
	@Test
	@DisplayName("fail: password가 null일 때 저장 실패")
	void failOnNullPassword() {
		// given
		User user = User.builder()
			.username(SharedData.VALID_USERNAME)
			.email(SharedData.VALID_EMAIL)
			.password(null) // nullable = false 제약 조건 위반
			.build();

		// when, then
		assertThrows(PersistenceException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}

	/*
	 * failOnNullRole
	 */
	@Test
	@DisplayName("fail: role이 null일 때 저장 실패")
	void failOnNullRole() {
		// given
		User user = User.builder()
			.username(SharedData.VALID_USERNAME)
			.email(SharedData.VALID_EMAIL)
			.password(SharedData.VALID_PASSWORD)
			.build();
		ReflectionTestUtils.setField(user, "role", null); // nullable = false 제약 조건 위반

		// when, then
		assertThrows(PersistenceException.class, () -> {
			entityManager.persist(user);
			entityManager.flush();
		});
	}
}