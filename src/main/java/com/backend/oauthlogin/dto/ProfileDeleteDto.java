package com.backend.oauthlogin.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfileDeleteDto {

    private Long profileId;

    private boolean activated;
}
