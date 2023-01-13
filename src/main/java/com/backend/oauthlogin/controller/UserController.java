package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.exception.BaseResponse;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequestMapping("/simya")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/form-signup")
    public BaseResponse<UserDto> signup(@Valid @RequestBody UserDto userDto) {
        try {
            return new BaseResponse<>(userService.formSignup(userDto));
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/form-login")
    public BaseResponse<TokenDto> formLogin(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) {
        try{
            TokenDto tokenDto = authService.login(loginDto);
            String accessToken = tokenDto.getAccessToken();
            String refreshToken = tokenDto.getRefreshToken();
            response.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
//            response.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Refresh  " + refreshToken);// 토큰 헤더에 넣기
            return new BaseResponse<>(tokenDto);
        } catch (BaseException e) {
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // USER, ADMIN 권한 모두 허용
    public BaseResponse<User> getMyUserInfo() {
        User userResponse = userService.getMyUserWithAuthorities().get();
        return new BaseResponse<>(userResponse);
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")   // ADMIN 권한만 허용 -> API를 호출 가능한 권한을 제한함
    public BaseResponse<User> getUserInfo(@PathVariable String username) {
        User userResponse = userService.getUserWithAuthorities(username).get();
        return new BaseResponse<>(userResponse);
    }
}
