package com.yj.jwtauth.domain.user.repository;

import com.yj.jwtauth.common.datas.SharedData;
import com.yj.jwtauth.common.entity.User;
import com.yj.jwtauth.domain.user.enums.UserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

	@Autowired
	private TestEntityManager entityManager;

	@Autowired
	private UserRepository userRepository;

	// SharedData에서 생성한 User 객체의 ID를 제거하는 헬퍼 메서드
	private User prepareUserForPersist(User user) {
		// ID를 null로 설정하여 새로운 엔티티로 처리되도록 함
		ReflectionTestUtils.setField(user, "id", null);
		return user;
	}

	/*
	 * findByEmail
	 */
	@Test
	@DisplayName("success: 이메일로 유저 조회 성공")
	void findByEmail() {
		// given
		User user = prepareUserForPersist(SharedData.createValidUserEntity());
		entityManager.persist(user);
		entityManager.flush();

		// when
		Optional<User> result = userRepository.findByEmail(user.getEmail());

		// then
		assertThat(result).isPresent();
		assertThat(result.get().getEmail()).isEqualTo(SharedData.VALID_EMAIL);
		assertThat(result.get().getUsername()).isEqualTo(SharedData.VALID_USERNAME);
		assertThat(result.get().getRole()).isEqualTo(UserRole.USER);
	}

	@Test
	@DisplayName("fail: 존재하지 않는 이메일로 유저 조회 시 빈 Optional 반환")
	void findByEmailNotFound() {
		// given
		String nonExistentEmail = "nonexistent@example.com";

		// when
		Optional<User> result = userRepository.findByEmail(nonExistentEmail);

		// then
		assertThat(result).isNotPresent();
	}

	/*
	 * existsByEmail
	 */
	@Test
	@DisplayName("success: 이미 가입된 이메일 확인 - 존재함")
	void existsByEmailTrue() {
		// given
		User user = prepareUserForPersist(SharedData.createValidUserEntity());
		entityManager.persist(user);
		entityManager.flush();

		// when
		boolean exists = userRepository.existsByEmail(user.getEmail());

		// then
		assertThat(exists).isTrue();
	}

	@Test
	@DisplayName("success: 이미 가입된 이메일 확인 - 존재하지 않음")
	void existsByEmailFalse() {
		// given
		String nonExistentEmail = "nonexistent@example.com";

		// when
		boolean exists = userRepository.existsByEmail(nonExistentEmail);

		// then
		assertThat(exists).isFalse();
	}
}