package com.backend.oauthlogin.dto.oauth;

import com.backend.oauthlogin.entity.User;
import lombok.Data;

@Data
public class RefreshResponseDto {

    String accessToken;
    User user;
}
