package com.backend.oauthlogin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
public class ProfileUpdateDto {

    private Long profileId;

    private String nickname;
    
    private String comment;

    private String picture;

    private boolean isRepresent;
}
