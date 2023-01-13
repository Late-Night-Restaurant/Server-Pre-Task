package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.oauth.kakao.KakaoTokenDto;
import com.backend.oauthlogin.exception.BaseResponse;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequiredArgsConstructor
public class OauthController {

    private final OauthService oauthService;
    private final AuthService authService;

    // 프론트 -> 서버 로 인가코드 전송하면 받아오는 URI
    // 임시 URI 경로
    // https://kauth.kakao.com/oauth/authorize?client_id=4ab7b193bdd041a30446ede285a1f77a&redirect_uri=http://localhost:8080/api/authorization_code&response_type=code
    @GetMapping("/api/authorization_code")  // redirect uri로 code받아오고, access_token을 받아온다
    public ResponseEntity<TokenDto> getLogin(@RequestParam("code") String code) {
        // 인가코드를 넘겨주고 카카오 서버에게 액세스 토큰 발급 요청
        KakaoTokenDto kakaoTokenDto = oauthService.getKakaoAccessToken(code);
        // 발급받은 액세스 토큰으로 카카오 서버에 회원정보 요청 후 DB에 저장
        TokenDto jwtTokenDto = oauthService.saveKakaoUser(kakaoTokenDto.getAccess_token());
        HttpHeaders headers = authService.inputTokenInHeader(jwtTokenDto);

        return ResponseEntity.ok().headers(headers).body(jwtTokenDto);
    }

    @GetMapping("/api/code")
    public BaseResponse<String> code(@RequestParam("code") String code) {
        String message = "성공적으로 카카오 유저 토큰 발급이 완료되었습니다.";

        return new BaseResponse<>(message);
    }

    // 인가코드 과정 없이 바로 맥세스 토큰 받아오기
    public ResponseEntity<TokenDto> getKakaoAccessToken(@RequestParam("token") String token) {
        TokenDto jwtTokenDto = oauthService.saveKakaoUser(token);
        HttpHeaders headers = authService.inputTokenInHeader(jwtTokenDto);

        return ResponseEntity.ok().headers(headers).body(jwtTokenDto);
    }
}
