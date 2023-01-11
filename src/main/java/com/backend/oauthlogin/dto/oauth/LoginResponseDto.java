package com.backend.oauthlogin.dto.oauth;

import com.backend.oauthlogin.entity.User;
import lombok.Data;

@Data
public class LoginResponseDto {

    public boolean loginSuccess;
    public User user;
    public String kakaoAccessToken;
}
