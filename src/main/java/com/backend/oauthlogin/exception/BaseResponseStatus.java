package com.backend.oauthlogin.exception;

import lombok.Getter;

//성공적인 응답은 200번대
//클라이언트 오류 응답은 400번대
//서버 오류 응답은 500번대
//리디렉션 응답은 300번대
@Getter
public enum BaseResponseStatus {

    // 200 ~ : 성공
    SUCCESS(true, 200, "요청에 성공하였습니다."),


    // 400 ~ :  클라이언트 Request 오류
    // 400 : Bad Request
    // 401 : Unauthorized
    // 403 : Forbidden
    // 404 : Not Found
    REQUEST_ERROR(false, 400, "입력값을 확인해주세요"),
    EMPTY_JWT(false, 401, "JWT를 입력해주세요."),
    INVALID_JWT(false, 403, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false, 403, "권한이 없는 유저의 접근입니다."),

    // user 관련
    USERS_EMPTY_USER_ID(false, 400, "유저 아이디 값을 확인해주세요"),
    POST_USERS_EMPTY_EMAIL(false, 400, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 400, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false, 400, "이 이메일로 가입한 유저가 존재합니다."),
    DUPLICATED_EMAIL(false, 400, "중복된 이메일입니다."),

    POST_USERS_EMPTY_PASSWORD(false, 400, "비밀번호를 입력해주세요."),
    USERS_PASSWORD_FORMAT(false, 400, "비밀번호는 8자 이상의 영문 대소문자와 특수문자로 구성해야 합니다."),


    // user 관련
    FAILED_TO_LOGIN(false, 404, "존재하지 않는 아이디이거나 비밀번호가 틀렸습니다."),
    BANNED_USER_IN_LOGIN(false, 403, "정지된 유저이므로 로그인이 불가합니다."),



    // 500 ~ : Database, Server 오류
    // 500 : Internal Server Error
    // 503 : Service Unavailable
    DATABASE_ERROR(false, 503, "데이터베이스 연결에 실패했습니다."),
    SERVER_ERROR(false, 503, "서버와의 연결에 실패했습니다."),

    // [PATCH] user 정보 수정 시
    MODIFY_FAIL_USERNAME(false, 500, "회원 이름을 변경하는 데 실패했습니다."),
    MODIFY_FAIL_POSTS_INFO(false, 500, "게시글 정보 수정에 실패했습니다."),
    DELETE_FAIL_POST(false, 500, "게시글 삭제에 실패했습니다."),
    PASSWORD_ENCRYPTION_ERROR(false, 500, "비밀번호 암호화에 실패했습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 500, "비밀번호 복호화에 실패했습니다.");


    private final boolean isSuccess;
    private final int code;
    private final String message;

    BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
