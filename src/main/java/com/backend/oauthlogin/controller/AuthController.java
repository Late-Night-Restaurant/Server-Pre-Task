package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.TokenRequestDto;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/reissue")
    public Response reissue(@RequestBody TokenRequestDto tokenRequestDto) {
        return Response.success(authService.reissue(tokenRequestDto));
    }

}
