package com.yj.jwtauth.domain.user.service;

import com.yj.jwtauth.auth.JwtService;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;

	@Override
	@Transactional
	public SignupResponseDto userSignup(SignupRequestDto requestDto) {
		logger.debug("User signup for email: {}", requestDto.getEmail());
		validateDuplicateEmail(requestDto.getEmail());

		User user = User.builder()
			.username(requestDto.getUsername())
			.email(requestDto.getEmail())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.build();

		User savedUser = userRepository.save(user);
		logger.debug("User saved with ID: {}", savedUser.getId());

		return toSignupResponse(savedUser);
	}

	@Override
	@Transactional
	public SignupResponseDto adminSignup(SignupRequestDto requestDto) {
		logger.debug("Admin signup for email: {}", requestDto.getEmail());
		validateDuplicateEmail(requestDto.getEmail());

		User admin = User.builder()
			.username(requestDto.getUsername())
			.email(requestDto.getEmail())
			.password(passwordEncoder.encode(requestDto.getPassword()))
			.build();

		admin.updateRole(UserRole.ADMIN);
		User savedAdmin = userRepository.save(admin);
		logger.debug("Admin saved with ID: {}", savedAdmin.getId());

		return toSignupResponse(savedAdmin);
	}

	@Override
	public SigninResponseDto userOrAdminSignin(SigninRequestDto requestDto) {
		logger.debug("Signin attempt for email: {}", requestDto.getEmail());
		User user = userRepository.findByEmail(requestDto.getEmail())
			.orElseThrow(() -> {
				logger.warn("User not found for email: {}", requestDto.getEmail());
				return new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
			});

		if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
			logger.warn("Invalid password for email: {}", requestDto.getEmail());
			throw new CustomRuntimeException(ErrorCode.INVALID_CREDENTIALS);
		}

		String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
		logger.debug("Generated token for user ID: {}", user.getId());
		return new SigninResponseDto(token);
	}

	@Override
	@Transactional
	public UserResponseDto assignAdminRole(Long userId, RoleUpdateRequestDto dto) {
		logger.debug("Assigning role {} to user ID: {}", dto.getRole(), userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				logger.error("User not found for ID: {}", userId);
				return new CustomRuntimeException(ErrorCode.ACCESS_DENIED);
			});

		user.updateRole(dto.getRole());
		logger.debug("Updated role for user ID: {}", userId);

		return new UserResponseDto(user) {};
	}

	@Override
	public UserResponseDto getMyInfo(Long userId) {
		logger.debug("Fetching info for user ID: {}", userId);
		User user = userRepository.findById(userId)
			.orElseThrow(() -> {
				logger.error("User not found for ID: {}", userId);
				return new CustomRuntimeException(ErrorCode.ACCESS_DENIED);
			});

		logger.debug("User found: email={}, role={}", user.getEmail(), user.getRole());
		return new UserResponseDto(user) {};
	}

	private void validateDuplicateEmail(String email) {
		if (userRepository.existsByEmail(email)) {
			logger.warn("Email already exists: {}", email);
			throw new CustomRuntimeException(ErrorCode.EMAIL_ALREADY_EXISTS);
		}
	}

	private SignupResponseDto toSignupResponse(User user) {
		return SignupResponseDto.builder()
			.id(user.getId())
			.username(user.getUsername())
			.email(user.getEmail())
			.role(user.getRole())
			.createdAt(user.getCreatedAt())
			.updatedAt(user.getUpdatedAt())
			.build();
	}
}