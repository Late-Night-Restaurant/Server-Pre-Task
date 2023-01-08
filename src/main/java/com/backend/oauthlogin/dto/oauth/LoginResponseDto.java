package com.backend.oauthlogin.dto.oauth;

import com.backend.oauthlogin.entity.Account;
import lombok.Data;

@Data
public class LoginResponseDto {

    public boolean loginSuccess;
    public Account account;
    public String kakaoAccessToken;
}
