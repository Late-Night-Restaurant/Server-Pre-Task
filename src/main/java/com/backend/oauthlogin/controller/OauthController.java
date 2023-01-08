package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.config.BaseResponse;
import com.backend.oauthlogin.dto.oauth.LoginResponseDto;
import com.backend.oauthlogin.dto.oauth.SignupRequestDto;
import com.backend.oauthlogin.dto.oauth.SignupResponseDto;
import com.backend.oauthlogin.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class OauthController {

    private final OauthService oauthService;


    //==  Kakao 로그인, 회원가입 ==//

    /**
     * 인가 코드로 카카오 서버에 Access Token 을 요청하고, 해당 토큰으로 유저 정보를 받아와 DB 에 저장하는 API
     * -> GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직 수행
     */
    /*@GetMapping("/login/kakao")
    public BaseResponse<LoginResponseDto> kakaoLogin(HttpServletRequest request) {

        String code = request.getParameter("code");
        String kakaoAccessToken = oauthService.getKakaoAccessToken(code).getAccessToken();
        return new BaseResponse<>(oauthService.kakaoLogin(kakaoAccessToken));
    }

    @PostMapping("/signup/kakao")
    public BaseResponse<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) {
        return new BaseResponse<>(oauthService.kakaoSignup(requestDto));
    }*/

}
