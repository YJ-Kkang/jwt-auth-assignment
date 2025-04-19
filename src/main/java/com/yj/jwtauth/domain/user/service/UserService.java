package com.yj.jwtauth.domain.user.service;

import com.yj.jwtauth.domain.user.dto.request.SignupRequestDto;
import com.yj.jwtauth.domain.user.dto.request.SigninRequestDto;
import com.yj.jwtauth.domain.user.dto.request.RoleUpdateRequestDto;
import com.yj.jwtauth.domain.user.dto.response.SignupResponseDto;
import com.yj.jwtauth.domain.user.dto.response.SigninResponseDto;
import com.yj.jwtauth.domain.user.dto.response.UserResponseDto;

public interface UserService {

	SignupResponseDto userSignup(SignupRequestDto requestDto);

	SignupResponseDto adminSignup(SignupRequestDto requestDto);

	SigninResponseDto userOrAdminSignin(SigninRequestDto requestDto);

	UserResponseDto assignAdminRole(Long userId, RoleUpdateRequestDto roleUpdateRequestDto);

	UserResponseDto getMyInfo(Long userId);
}