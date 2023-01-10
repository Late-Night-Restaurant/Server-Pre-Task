package com.backend.oauthlogin.service;

import com.backend.oauthlogin.config.BaseException;
import com.backend.oauthlogin.config.BaseResponseStatus;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.oauth.SignupRequestDto;
import com.backend.oauthlogin.dto.oauth.kakao.KakaoAccountDto;
import com.backend.oauthlogin.dto.oauth.kakao.KakaoTokenDto;
import com.backend.oauthlogin.entity.RefreshToken;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.repository.RefreshTokenRepository;
import com.backend.oauthlogin.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


import static com.backend.oauthlogin.entity.Role.ROLE_USER;
import static com.backend.oauthlogin.entity.SocialLoginType.KAKAO;

@Service
@Slf4j
@RequiredArgsConstructor
public class OauthService {
    private final UserRepository userRepository;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final UserRepository userRepository;
    private final RefreshTokenRepository tokenRepository;
    private final AuthService authService;

    // 환경변수 가져오기
    @Value("${kakao.key}")
    String KAKAO_CLIENT_ID;

    @Value("${kakao.redirect_uri}")
    String KAKAO_REDIRECT_URI;


    // 인가코드로 Kakao Access Token 을 요청하는 메소드
    public KakaoTokenDto getKakaoAccessToken(String code) {

        RestTemplate rt = new RestTemplate();  // 통신용 템플릿 for HTTP 통신 단순화 (스프링에서 지원하는 REST 서비스 호출방식)
        HttpHeaders headers = new HttpHeaders();
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        /* Kakao 공식 문서에 따라 헤더,바디 값 구성 */
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        params.add("grant_type", "authorization_code");
        params.add("client_id", KAKAO_CLIENT_ID);
        params.add("redirect_uri", KAKAO_REDIRECT_URI);
        params.add("code", code);  // 인가 코드 요청 시 받은 인가 코드 값 from 프론트엔드

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);
        log.info("KakaoTokenRequest: {}", kakaoTokenRequest);

        // Kakao 로부터 Access Token 수신
        ResponseEntity<String> accessTokenResponse = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // JSON Parsing (KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoTokenDto kakaoTokenDto = null;
        try {
            kakaoTokenDto = objectMapper.readValue(accessTokenResponse.getBody(), KakaoTokenDto.class);
        } catch (JsonProcessingException e) {
            log.debug("Kakao Access Token(KakaoTokenDto) JSON Parsing 에 실패했습니다.");
        }

        return kakaoTokenDto;
    }

    // Kakao Access Token 으로 카카오 서버에 정보 요청하는 메소드
    public User getKakaoInfo(String kakaoAccessToken) {

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + kakaoAccessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> accountInfoRequest = new HttpEntity<>(headers);

        // POST 방식으로 API 서버에 요청을 보내고 Response 를 받아온다.
        ResponseEntity<String> accountInfoResponse = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                accountInfoRequest,
                String.class
        );

        log.info("카카오 서버에서 정상적으로 데이터를 수신했습니다.");

        // Json Parsing (KakaoAccountDto)
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoAccountDto kakaoAccountDto = null;
        try {
            kakaoAccountDto = objectMapper.readValue(accountInfoResponse.getBody(), KakaoAccountDto.class);
        } catch (JsonProcessingException e) {
            log.debug("KakaoInfo(KakaoAccountDto) JSON Parsing 에 실패했습니다.");
        }

        // KakaoAccountDto 에서 필요한 정보를 꺼내서 Account 객체로 매핑
        String email = kakaoAccountDto.getKakaoAccount().getEmail();
        String kakaoName = kakaoAccountDto.getKakaoAccount().getProfile().getNickname();

        return User.builder()
                .email(email)
                .password("change your password!")
                .socialLoginType(KAKAO)
                .role(ROLE_USER)
                .activated(true)
                .build();
    }

    public User saveKakaoUser(SignupRequestDto requestDto) {

        User newUser = User.builder()
                .email(requestDto.getUser().getEmail())
                .password("change your password!")
                .socialLoginType(KAKAO)
                .role(ROLE_USER)
                .activated(true)
                .build();
        userRepository.save(newUser);

        return newUser;
    }

    public HttpHeaders setTokenHeaders(TokenDto tokenDto) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie cookie = ResponseCookie.from("RefreshToken", tokenDto.getRefreshToken())
                .path("/")
                .maxAge(60*60*24*7)   // Cookie 유효기간 7일로 지정
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        headers.add("Set-cookie", cookie.toString());
        headers.add("Authorization", tokenDto.getAccessToken());

        return headers;
    }

    public void saveRefreshToken(User user, TokenDto tokenDto) {
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getUserId())
                .value(tokenDto.getRefreshToken())
                .build();

        tokenRepository.save(refreshToken);
        log.info("토큰 저장이 완료되었습니다 : 계정 아이디 - {}, refresh token - {}", user.getUserId(), tokenDto.getRefreshToken());
    }


}
