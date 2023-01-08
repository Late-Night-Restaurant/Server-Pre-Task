package com.backend.oauthlogin.dto.oauth;

import com.backend.oauthlogin.entity.Account;
import lombok.Data;

@Data
public class RefreshResponseDto {

    String accessToken;
    Account account;
}
