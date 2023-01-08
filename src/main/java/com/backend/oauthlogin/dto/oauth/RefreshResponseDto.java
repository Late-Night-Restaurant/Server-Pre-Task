package com.backend.oauthlogin.dto.oauth;

import jjun.server.jwttutorial.entity.Account;
import lombok.Data;

@Data
public class RefreshResponseDto {

    String accessToken;
    Account account;
}
