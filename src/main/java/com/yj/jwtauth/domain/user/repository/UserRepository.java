package com.yj.jwtauth.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.yj.jwtauth.common.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	// 이메일로 유저 조회
	Optional<User> findByEmail(String email);

	// 이미 가입된 이메일인지 체크
	boolean existsByEmail(String email);
}