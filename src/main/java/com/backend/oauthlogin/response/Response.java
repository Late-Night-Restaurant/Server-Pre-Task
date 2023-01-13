package com.backend.oauthlogin.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.backend.oauthlogin.response.ResponseStatus.*;

@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
@JsonInclude(JsonInclude.Include.NON_NULL)// Null값 버림
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter

public class Response {

    @JsonProperty("isSuccess")   // 속성명 지정
    private final Boolean isSuccess;
    private final int code;
    private final String message;
    @JsonInclude(JsonInclude.Include.NON_NULL)  // null이 아니면 포함 => 응답으로 전달할 값들
    private Object data;

    //    데이터 없이 성공 반환
    public static Response success(){
        return new Response(SUCCESS.isSuccess(),
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                null);
    }

    //    데이터 포함해서 성공 반환
    public static Response success(Object data){
        return new Response(SUCCESS.isSuccess(),
                SUCCESS.getCode(),
                SUCCESS.getMessage(),
                data);
    }

    //    에러 발생시 반환해주는 경우
    public static Response failure(ResponseStatus responseStatus){
        return new Response(responseStatus.isSuccess(),
                responseStatus.getCode(),
                responseStatus.getMessage(),
                null);
    }
}
