package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.LoginType;
import com.backend.oauthlogin.entity.Role;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.repository.UserRepository;
import com.backend.oauthlogin.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 회원가입, 유저정보조회 등의 API를 구현하기 위한 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        User loadedUser = userRepository.findUserByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
        if (!loadedUser.isActivated()) {
            throw new RuntimeException(email + " -> 활성화되어 있지 않습니다.");
        }
        return loadedUser;
    }

    // 회원가입 로직
    @Transactional
    public UserDto signup(UserDto userDto) {
        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .loginType(LoginType.FORM)
                .role(Role.ROLE_USER)
                .activated(true)
                .build();

        userRepository.save(user);

        return UserDto.builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .build();
    }

    @Transactional(readOnly = true)
    public void checkDuplicatedUser(UserDto userDto) {
        if (userRepository.findUserByEmail(userDto.getEmail()).orElse(null) != null) {
            throw new RuntimeException("이미 가입되어 있는 유저입니다.");
        }
    }

    @Transactional(readOnly = true)
    public UserDto formLogin(UserDto userDto) {

        return null;
    }


    //== 유저, 권한정보를 가져오는 메소드 ==//
    // username을 기준으로 정보를 가져온다.

    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

//    @Transactional(readOnly = true)
//    public Optional<User> getMyUserWithAuthorities() {
//        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
//    }


//    @Transactional(readOnly = true)
//    public Optional<User> getUserWithAuthorities(String email) {
//        return userRepository.findOneWithAuthoritiesByUsername(email);
//    }
//
//    // SecurityContext에 저장된 username에 해당하는 유저, 권한의 정보만 가저온다.
//    @Transactional(readOnly = true)
//    public Optional<User> getMyUserWithAuthorities() {
//        return SecurityUtil.getCurrentUsername().flatMap(userRepository::findOneWithAuthoritiesByUsername);
//    }


}
