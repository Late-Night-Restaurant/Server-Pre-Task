package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.LoginType;
import com.backend.oauthlogin.entity.Role;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.repository.UserRepository;
import com.backend.oauthlogin.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 회원가입, 유저정보조회 등의 API를 구현하기 위한 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입 로직
    @Transactional
    public User signup(UserDto userDto) {
        // username을 기준으로 이미 DB에 존재하는 유저인지 검사
//        if (userRepository.findOneWithAuthoritiesByEmail(userDto.getEmail()).orElse(null) != null) {
//            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
//        }

        User user = User.builder()
                .email(userDto.getEmail())
                .pw(passwordEncoder.encode(userDto.getPw()))
                .loginType(LoginType.FORM)
                .role(Role.ROLE_USER)
                .activated(true)
                .build();

        return userRepository.save(user);   // 없으면 새로 유저 정보와 권한 정보 생성 후, DB에 저장
    }

    //== 유저, 권한정보를 가져오는 메소드 ==//
    // email 을 기준으로 정보를 가져온다.
    @Transactional(readOnly = true)
    public Optional<User> getUserWithAuthorities(String username) {
        return userRepository.findOneWithAuthoritiesByEmail(username);
    }

    // SecurityContext에 저장된 email 에 해당하는 유저, 권한의 정보만 가저온다.
    @Transactional(readOnly = true)
    public Optional<User> getMyUserWithAuthorities() {
        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByEmail);
    }

}
