package com.backend.oauthlogin.dto;

import com.backend.oauthlogin.entity.Role;
import com.backend.oauthlogin.entity.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


@Getter @Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @NotNull
    @Size(min = 3, max = 50)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull
    @Size(min = 3, max = 100)
    private String password;

    public static UserDto from(User user) {
        if (user == null) return null;

        return UserDto.builder()
                .email(user.getEmail())
                .build();
    }

}
