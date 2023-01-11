package com.backend.oauthlogin.service;


import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.TokenRequestDto;
import com.backend.oauthlogin.dto.oauth.SignupRequestDto;
import com.backend.oauthlogin.entity.RefreshToken;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.exception.BaseException;
import com.backend.oauthlogin.exception.BaseResponseStatus;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.jwt.TokenProvider;
import com.backend.oauthlogin.repository.RefreshTokenRepository;
import com.backend.oauthlogin.repository.UserRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.security.Key;

import static com.backend.oauthlogin.exception.BaseResponseStatus.EMPTY_JWT;
import static com.backend.oauthlogin.exception.BaseResponseStatus.INVALID_JWT;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;

    //== 일반 Form Login 토큰 발급 로직 ==//

    /**
     * 사용자 정보를 가져오는 메소드
     * Request Header 에 Authorization 항목으로 토큰이 오면, 인증된 사용자에 대해 정보를 가져와 Account 타입으로 반환
     */
//    @Transactional
//    public User getAccountInfo(HttpServletRequest request) {
//        String authenticAccount = (String) request.getAttribute("authenticAccount");
//        User user = userRepository.findByEmail(authenticAccount).orElseThrow();
//        System.out.println("AccountService 실행: " + user);
//        return user;
//    }

    /**
     * 로그인 시 Token 을 발급해서 리턴하는 메소드
     */
//    @Transactional
//    public TokenDto authenticate(LoginDto loginDto) {
//        // 1. Login 을 시도한 ID/PW 를 기반으로 AuthenticationToken 생성
//        UsernamePasswordAuthenticationToken authenticationToken =
//                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());
//        System.out.println("AuthService - UsernamePassordAuthenticationToken 객체 생성 " + authenticationToken);
//        // 2. 실제로 검증이 이루어지는 부분 (유저의 비밀번호 일치 여부 체크)
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);   // 이때 커스텀한 UserDetailsService 의 loadByUsername 메소드가 실행
//
//        System.out.println("createToken() 실행 전");
//        // 3. 인증 정보를 기반으로 JWT 토큰 생성
//        TokenDto tokenDto = tokenProvider.createToken(authentication);
//        System.out.println("token 발급 성공: " + tokenDto.getAccessToken() + " Refresh Token 발급 전");
//
//        // 4. Refresh Token 저장
//        RefreshToken refreshToken = RefreshToken.builder()
//                .key(authentication.getName())
//                .value(tokenDto.getRefreshToken())
//                .build();
//        System.out.println("Refresh Token : " + refreshToken.getValue());
//
//        refreshTokenRepository.save(refreshToken);
//
//        return tokenDto;
//    }

    @Transactional
    public Authentication authenticateProto(LoginDto loginDto) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword());
        return authenticationManagerBuilder.getObject().authenticate(authenticationToken);
    }

    //토큰 발행하는 데에 email만 필요하다면 Authentication을 매개변수로 굳이 줄 필요가 없다!
    // 리프레쉬 토큰에 값 넣고 저장
    @Transactional
    public TokenDto authorizeProto(String email) {
        TokenDto tokenDto = tokenProvider.createToken(email);

        RefreshToken refreshToken = RefreshToken.builder()
                .key(email)
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }

    // 토큰을 헤더에 넣는다? 리프레쉬랑 엑세스 둘다? TokenDto는 어떻게 쓰여야 하는가, 만료시간 체크 해야
    @Transactional
    public HttpHeaders inputTokenInHeader(TokenDto tokenDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Refresh  " + tokenDto.getRefreshToken());
        return httpHeaders;
    }

    // 로그인한 사용자 여부에 대한 검증 시 Header 에서 토큰 값을 가져온다.
    public String getJwt() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        log.info("AuthService - getJwt() : header에 저장된 Access Token {}", request.getHeader("Authorization"));
        return request.getHeader("Authorization").substring(7);
    }

    public String getUsername() throws BaseException {
        String accessToken = getJwt();
        if (accessToken == null) {
            throw new BaseException(EMPTY_JWT);
        }
        // TODO Access Token 만료 여부 체크 로직 추가

        Claims claims;

        try {
            claims = tokenProvider.parseClaims(accessToken);
        } catch (Exception e) {
            throw new BaseException(INVALID_JWT);
        }
        log.info("AuthService - getUsername() : {}", claims);

        return claims.get("sub", String.class);
    }

    public User authenticateUser() throws BaseException {
        String username = getUsername();
        return userRepository.findByEmail(username).orElseThrow();
    }

    public boolean validateClaim(String accessToken) {
        try {
            Claims claims = tokenProvider.parseClaims(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            // TODO 토큰 만료 시, TokenRequestDto (AccessToken, RefreshToken 넘겨서 갱신)
            return false;
        }

    }




//    @Transactional
//    public TokenDto oauthenticate(String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow();
//        log.info("AuthService-login: 계정을 찾았습니다 {}", user);
//
//        // 토큰 발행
//        TokenDto tokenDto = tokenProvider.createToken(email);
//
//        // RefreshToken DB 에 저장
//        RefreshToken refreshToken = RefreshToken.builder()
//                .key(user.getUsername())
//                .value(tokenDto.getRefreshToken())
//                .build();
//
//        refreshTokenRepository.save(refreshToken);
//        log.info("토큰 발급과 저장을 완료했습니다.");
//
//        return tokenDto;
//
//    }

    /**
     * 토큰 만료 시 재발급하는 메소드
     */
    @Transactional
    public TokenDto reissue(TokenRequestDto tokenRequestDto) {
        // 1. Refresh Token 검증
        if (!tokenProvider.validateToken(tokenRequestDto.getRefreshToken())) {
            log.debug("Refresh Token 이 유효하지 않습니다.");
        }

        // 2. Access Token 에서 유저 정보 가져오기
        Authentication authentication = tokenProvider.getAuthentication(tokenRequestDto.getAccessToken());

        // 3. 저장소에서 유저 ID 를 기반으로 Refresh Token 값을 가져오기
        RefreshToken refreshToken = refreshTokenRepository.findByKey(authentication.getName())
                .orElseThrow(() -> new RuntimeException("로그아웃된 사용자입니다."));

        // 4. Refresh Token 일치하는지 검사
        if (!refreshToken.getValue().equals(tokenRequestDto.getRefreshToken())) {
            log.debug("토큰의 유저 정보가 일치하지 않습니다.");
        }

        // 5. 새로운 토큰 생성
        TokenDto tokenDto = tokenProvider.createToken(authentication.getName());

        // 6. Repository 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateToken(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    /**
     * 회원가입 요청에 대해 Access Token 과 Refresh Token 을 방급하고, Refresh Token 을 리포지토리에 저장하는 메소드
     */
    public TokenDto oauthSignup(SignupRequestDto requestDto) {
        User user = requestDto.getUser();
        return tokenProvider.createToken(user.getEmail());
    }


    public User findUser(String email) {
        return userRepository.findByEmail(email).orElseThrow();
    }

}
