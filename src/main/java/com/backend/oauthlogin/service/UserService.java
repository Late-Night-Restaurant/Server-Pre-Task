package com.backend.oauthlogin.service;

import com.backend.oauthlogin.dto.LoginDto;
import com.backend.oauthlogin.dto.UserDto;
import com.backend.oauthlogin.entity.LoginType;
import com.backend.oauthlogin.entity.Role;
import com.backend.oauthlogin.entity.User;
import com.backend.oauthlogin.repository.UserRepository;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.backend.oauthlogin.response.ResponseStatus.POST_USERS_EXISTS_EMAIL;
import static com.backend.oauthlogin.response.ResponseStatus.SUCCESS;

/**
 * 회원가입, 유저정보조회 등의 API를 구현하기 위한 Service 클래스
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Response formSignup(UserDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            return Response.failure(POST_USERS_EXISTS_EMAIL);
        } else {
            User newUser = User.builder()
                    .email(userDto.getEmail())
                    .pw(passwordEncoder.encode(userDto.getPassword()))
                    .loginType(LoginType.FORM)
                    .role(Role.ROLE_USER)
                    .activated(true)
                    .build();

            return Response.success(userRepository.save(newUser).getEmail());
        }
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
