package com.backend.oauthlogin.service;


import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.TokenRequestDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.RefreshToken;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.jwt.TokenProvider;
import com.backend.oauthlogin.repository.RefreshTokenRepository;
import com.backend.oauthlogin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;

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
    @Transactional
    public User getUserInfo(HttpServletRequest request) {
        String authenticAccount = (String) request.getAttribute("authenticAccount");
        User user = userRepository.findUserByEmail(authenticAccount).orElseThrow();
        System.out.println("AccountService 실행: " + user);
        return user;
    }

    /**
     * 로그인 시 Token 을 발급해서 리턴하는 메소드
     */
    @Transactional
    public TokenDto authenticate(UserDto userDto) {
        // 1. Login 을 시도한 ID/PW 를 기반으로 AuthenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword());
        // 2. 실제로 검증이 이루어지는 부분 (유저의 비밀번호 일치 여부 체크)
        authenticationManagerBuilder.getObject().authenticate(authenticationToken);// 이때 커스텀한 UserDetailsService 의 loadByUsername 메소드가 실행

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        return publishToken(userDto);
    }


    @Transactional
    public TokenDto authenticate(String email) {

        User user = userRepository.findUserByEmail(email)
                .orElseThrow();
        log.info("AuthService-login: 계정을 찾았습니다 {}", user);

        // 토큰 발행
        TokenDto tokenDto = tokenProvider.createToken(email);

        // RefreshToken DB 에 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .key(user.getUserId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);
        log.info("토큰 발급과 저장을 완료했습니다.");

        return tokenDto;
    }

//    @Transactional
//    public TokenDto authorize(UserDto userDto) {
//
//    }

    @Transactional
    public TokenDto publishToken(UserDto userDto) {

        TokenDto tokenDto = tokenProvider.createToken(userDto.getEmail());

        //
        RefreshToken refreshToken = RefreshToken.builder()
                .key(userRepository.findUserByEmail(userDto.getEmail()).orElseThrow(() ->
                        new UsernameNotFoundException("리프레쉬 토큰을 발급할 수 없습니다")).getUserId())
                .value(tokenDto.getRefreshToken())
                .build();

        refreshTokenRepository.save(refreshToken);

        return tokenDto;
    }


    @Transactional
    public void rememberUserByToken(TokenDto tokenDto) {
        Authentication authentication = tokenProvider.getAuthentication(tokenDto.getAccessToken());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }



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
        TokenDto tokenDto = tokenProvider.createToken(authentication);

        // 6. Repository 정보 업데이트
        RefreshToken newRefreshToken = refreshToken.updateToken(tokenDto.getRefreshToken());
        refreshTokenRepository.save(newRefreshToken);

        return tokenDto;
    }

    public HttpHeaders inputTokenInHeader(TokenDto tokenDto) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + tokenDto.getAccessToken());
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, "refresh " + tokenDto.getRefreshToken());
        return httpHeaders;
    }


    /**
     * 회원가입 요청에 대해 Access Token 과 Refresh Token 을 방급하고, Refresh Token 을 리포지토리에 저장하는 메소드
     */
//    public TokenDto oauthSignup(SignupRequestDto requestDto) {
//        Account account = requestDto.getAccount();
//        return tokenProvider.createToken(account.getEmail());
//    }




}
