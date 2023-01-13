package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.exception.BaseResponse;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.UserService;
import lombok.RequiredArgsConstructor;
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
    public BaseResponse<UserDto> signup(@Valid @RequestBody UserDto userDto) throws BaseException {
       try {
           return new BaseResponse<>(userService.formSignup(userDto));
       } catch (BaseException e) {
           throw new BaseException(e.getStatus());
       }
    }

    @GetMapping("/form-login")
    public BaseResponse<TokenDto> formLogin(@Valid @RequestBody LoginDto loginDto, HttpServletResponse response) throws BaseException {
        try{
            TokenDto tokenDto = authService.login(loginDto);
            String accessToken = tokenDto.getAccessToken();
            String refreshToken = tokenDto.getRefreshToken();
            response.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + accessToken);
            response.addHeader(JwtFilter.AUTHORIZATION_HEADER, "Refresh  " + refreshToken);// 토큰 헤더에 넣기
            return new BaseResponse<>(tokenDto);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

//    @GetMapping("/user")
//    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")  // USER, ADMIN 권한 모두 허용
//    public Response getMyUserInfo() {
//        return Response.success(userService.getMyUserWithAuthorities().get());
//    }

//    @GetMapping("/user/{username}")
//    @PreAuthorize("hasAnyRole('ADMIN')")   // ADMIN 권한만 허용 -> API를 호출 가능한 권한을 제한함
//    public Response getUserInfo(@PathVariable String username) {
//        return Response.success(userService.getUserWithAuthorities(username).get());
//    }
}
