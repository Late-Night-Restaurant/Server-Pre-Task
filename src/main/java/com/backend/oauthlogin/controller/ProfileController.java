package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.ProfileRequestDto;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.exception.BaseResponse;
import com.backend.oauthlogin.repository.UserRepository;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.ProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/users/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;
    private final AuthService authService;

    @PostMapping("")
    public BaseResponse<ProfileRequestDto> createProfile(@Valid @RequestBody ProfileRequestDto profileRequestDto) throws BaseException {

        User user = authService.authenticateUser();  // 현재 접속한 유저
        log.info("ProfileController - user: {}", user);
        if (user == null) {
            return new BaseResponse("존재하지 않는 사용자입니다.");   // TODO Custom Status ENUM 으로 만들어서 관리
        }
        profileRequestDto.setUserProfile(user);
        profileService.createProfile(profileRequestDto);

        return new BaseResponse<>(profileRequestDto);
    }

}
