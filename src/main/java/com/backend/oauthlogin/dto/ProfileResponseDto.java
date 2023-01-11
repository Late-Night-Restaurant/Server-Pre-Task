package com.backend.oauthlogin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfileResponseDto {

    private Long profileId;

    private String nickname;
}
