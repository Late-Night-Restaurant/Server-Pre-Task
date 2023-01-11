package com.backend.oauthlogin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

@JsonInclude(JsonInclude.Include.NON_NULL)
//null값을 가지는 필드는 JSON응답에 포함되지 않음
@Getter
@AllArgsConstructor
public class Success<T> implements Result {
    private T data; // T는 어떤 타입이 와도 상관없음을 의미
}
