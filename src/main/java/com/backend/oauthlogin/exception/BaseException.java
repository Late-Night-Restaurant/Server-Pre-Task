package com.backend.oauthlogin.exception;

import com.backend.oauthlogin.response.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class BaseException extends Exception{
    private BaseResponseStatus status;
}
