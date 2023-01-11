package com.backend.oauthlogin.dto;

import com.backend.oauthlogin.entity.Profile;
import com.backend.oauthlogin.entity.User;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
public class ProfileRequestDto {

    @NotNull
    @Size(min = 1, max = 20)
    private String nickname;

    private String comment;

    private String picture;

    private User user;

    public void setUserProfile(User user) {
        this.user = user;
    }

    public Profile toEntity() {
        return Profile.builder()
                .nickname(nickname)
                .comment(comment)
                .picture(picture)
                .user(user)
                .isRepresent(false)
                .activated(true)
                .build();
    }

}
