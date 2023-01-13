package com.backend.oauthlogin.dto;

import com.backend.oauthlogin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponseDto {

    private Long profileId;

    private String nickname;

    private User user;
}
