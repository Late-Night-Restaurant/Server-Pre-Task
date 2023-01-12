package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.config.BaseResponse;
import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/simya")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final AuthService authService;


    @PostMapping("/signup")
    public Response signup(@Valid @RequestBody UserDto userDto) {
        return Response.success(userService.signup(userDto));
    }

    @GetMapping("/form-login")
    public ResponseEntity<TokenDto> formLogin(@Valid @RequestBody LoginDto loginDto) {

        Authentication authentication = authService.authenticate(loginDto); // 인증
        TokenDto tokenDto = authService.authorize(authentication); // 인가, 토큰발행
        HttpHeaders headers = authService.inputTokenInHeader(tokenDto); // 토큰 헤더에 넣기

        return new ResponseEntity<>(tokenDto, headers, HttpStatus.OK);
    }

    @GetMapping("/user")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // USER, ADMIN 권한 모두 허용
    public Response getMyUserInfo() {
        return Response.success(userService.getMyUserWithAuthorities().get());
    }

    @GetMapping("/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN')")   // ADMIN 권한만 허용 -> API를 호출 가능한 권한을 제한함
    public Response getUserInfo(@PathVariable String username) {
        return Response.success(userService.getUserWithAuthorities(username).get());
    }
}
