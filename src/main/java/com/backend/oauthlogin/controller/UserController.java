package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.config.BaseResponse;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthService authService;

    /**
     * 회원가입 API
     * - UserDto를 파라미터로 받아 UserService의 signup 메소드 호출
     */
    // response를 커스텀 해야함
    @PostMapping("/signup")
    public ResponseEntity<UserDto> signup(@Valid @RequestBody UserDto userDto) {
        userService.checkDuplicatedUser(userDto); // 가입 유무 확인
        UserDto user = userService.signup(userDto); // 회원가입
        TokenDto tokenDto = authService.publishToken(user); // JWT Token 생성
        authService.rememberUserByToken(tokenDto); // Context에 Authentication 저장
        HttpHeaders headers = authService.inputTokenInHeader(tokenDto); // JWT Token 헤더에 넣기
        // Response Header 에 추가
        return new ResponseEntity<>(user, headers, HttpStatus.OK);
    }

    //로그인 로직 생각해보기
    @GetMapping("/form-login")
    public ResponseEntity<UserDto> login(@Valid @RequestBody UserDto userDto) {
        userService.formLogin(userDto);

        return null;
    }

    /**
     * 유저 정보, 권한 정보 조회 API
     * - My : 일반 사용자는 자신의 개인정보 조회만 가능
     */

//    @GetMapping("/user")
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_ADMIN')")  // USER, ADMIN 권한 모두 허용
//    public BaseResponse<User> getMyUserInfo() {
//        return new BaseResponse<>(userService.getMyUserWithAuthorities().get());
//}


//    @GetMapping("/user/{username}")
//    @PreAuthorize("hasAnyRole('ADMIN')")   // ADMIN 권한만 허용 -> API를 호출 가능한 권한을 제한함
//    public BaseResponse<User> getUserInfo(@PathVariable String username) {
//        return new BaseResponse<>(userService.getUserWithAuthorities(username).get());
//    }
}
