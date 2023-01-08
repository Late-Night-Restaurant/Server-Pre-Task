package com.backend.oauthlogin.dto.oauth;

import com.backend.oauthlogin.entity.Account;
import lombok.Data;

@Data
public class SignupResponseDto {

    Account account;
    String result;
}
