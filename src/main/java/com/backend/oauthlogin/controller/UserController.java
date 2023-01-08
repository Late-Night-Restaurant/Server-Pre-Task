package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.config.BaseResponse;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 회원가입 API
     * - UserDto를 파라미터로 받아 UserService의 signup 메소드 호출
     */
    @PostMapping("/signup")
    public BaseResponse<User> signup(@Valid @RequestBody UserDto userDto) {
        return new BaseResponse<>(userService.signup(userDto));
    }

    /**
     * 유저 정보, 권한 정보 조회 API
     * - My : 일반 사용자는 자신의 개인정보 조회만 가능
     */

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // USER, ADMIN 권한 모두 허용
    public BaseResponse<User> getMyUserInfo() {
        return new BaseResponse<>(userService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")   // ADMIN 권한만 허용 -> API를 호출 가능한 권한을 제한함
    public BaseResponse<User> getUserInfo(@PathVariable String username) {
        return new BaseResponse<>(userService.getUserWithAuthorities(username).get());
    }
}
