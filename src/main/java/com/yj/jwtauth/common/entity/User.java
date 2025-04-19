package com.yj.jwtauth.common.entity;

import org.hibernate.annotations.Comment;

import com.yj.jwtauth.domain.user.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "USERS")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

	@Comment("유저 식별자")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Comment("유저명")
	@Column(
		name = "username",
		nullable = false
	)
	private String username;

	@Comment("이메일")
	@Column(
		name = "email",
		nullable = false
	)
	private String email;

	@Comment("비밀번호")
	@Column(
		name = "password",
		nullable = false
	)
	private String password;

	@Comment("유저 역할")
	@Enumerated(EnumType.STRING)
	@Column(
		name = "user_role",
		nullable = false
	)
	private UserRole role;

	@Comment("유저 탈퇴 여부")
	@Column(name = "is_deleted", columnDefinition = "TINYINT")
	private boolean isDeleted;

	// 빌더 생성자
	@Builder
	public User(String username, String email, String password) {
		this.username = username;
		this.email = email;
		this.password = password;
		this.role = UserRole.USER; // 기본 생성 시 USER 역할 부여
		this.isDeleted = false; // 기본 생성 시 회원 상태 존재로 설정
	}

	// 역할(Role) 변경
	public void updateRole(UserRole role) {
		this.role = role;
	}
}
