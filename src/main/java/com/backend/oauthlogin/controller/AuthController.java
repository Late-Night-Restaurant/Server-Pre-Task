package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.config.BaseResponse;
import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.TokenRequestDto;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class AuthController {

    private final AuthService authService;

    /**
     *  로그인 API
     *  - LoginDto 의 username, password를 파라미터로 받고 이를 이용해 UsernamePasswordAuthenticationToken 생성
     *  - authenticationToken을 이용해 Authentication 객체 생성
     *  - authenticate() 메소드 실행 시, loadUserByUsername 메소드가 실행
     *  - 생성한 Authentication 객체는 SecurityContext 에 저장
     *  - Authentication 객체를 createToken() 메소드를 통해 JWT Token 발급
     */
    @PostMapping("/authenticate")
    public ResponseEntity<TokenDto> authorize(@Valid @RequestBody LoginDto loginDto) {

        Authentication authentication = authService.authenticateProto(loginDto); // 인증 (
        TokenDto tokenDto = authService.authorizeProto(authentication.getName()); // 인가
        HttpHeaders headers = authService.inputTokenInHeader(tokenDto); // 토큰 헤더에 넣기

        return new ResponseEntity<>(tokenDto, headers, HttpStatus.OK);   // ResponseBody 에도 실어서 응답을 반환
    }

    /**
     * 토큰 재발급 API
     * - Access Token, Refresh Token String 을 담고 있는 TokenRequestDto 를 파라미터로 넘겨줌
     */
    @PostMapping("/reissue")
    public Response reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return Response.success(authService.reissue(tokenRequestDto));
    }
}
// Response 타입 커스텀