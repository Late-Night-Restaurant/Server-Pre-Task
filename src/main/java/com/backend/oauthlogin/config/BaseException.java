package com.backend.oauthlogin.config;

import com.backend.oauthlogin.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BaseException extends Exception{
    private ResponseStatus status;
}
