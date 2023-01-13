package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.dto.TokenDto;
import com.backend.oauthlogin.dto.oauth.kakao.KakaoTokenDto;
import com.backend.oauthlogin.jwt.JwtFilter;
import com.backend.oauthlogin.response.Response;
import com.backend.oauthlogin.service.AuthService;
import com.backend.oauthlogin.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
//@RequestMapping("/auth")
public class OauthController {

    private final OauthService oauthService;
    private final AuthService authService;


    //==  Kakao 로그인, 회원가입 ==//

    /**
     * 인가 코드로 카카오 서버에 Access Token 을 요청하고, 해당 토큰으로 유저 정보를 받아와 DB 에 저장하는 API
     * -> GET 방식으로 param 에 들어오는 인가코드를 추출하여 처리 로직 수행
     */
    /*@GetMapping("/login/kakao")
    public ResponseEntity<LoginResponseDto> kakaoLogin(HttpServletRequest request) {

        String code = request.getParameter("code");
        String kakaoAccessToken = oauthService.getKakaoAccessToken(code).getAccess_token();

        // 로그인 로직 : 이전에 회원가입을 한 적이 있는지를 판단하여 분기 처리
        // Kakao Access Token 으로 회원정보 받아오기
        User user = oauthService.getKakaoInfo(kakaoAccessToken);

        LoginResponseDto loginResponseDto = new LoginResponseDto();
        loginResponseDto.setKakaoAccessToken(kakaoAccessToken);
        loginResponseDto.setUser(user);

        try {
            TokenDto tokenDto = authService.oauthenticate(user.getEmail());
            loginResponseDto.setLoginSuccess(true);

            HttpHeaders headers = oauthService.setTokenHeaders(tokenDto);
            return ResponseEntity.ok().headers(headers).body(loginResponseDto);
        } catch (Exception e) {
            loginResponseDto.setLoginSuccess(false);
            return ResponseEntity.ok(loginResponseDto);
        }
    }


    // 회원가입 요청 처리 메소드
    @PostMapping("/signup/kakao")
    public ResponseEntity<SignupResponseDto> kakaoSignup(@RequestBody SignupRequestDto requestDto) {

        // 카카오 서버로부터 받아온 회원정보 DB 에 저장
        User newUser = oauthService.saveKakaoUser(requestDto);

        // 회원가입 상황에 따라 토큰 발급 후 헤더와 쿠키에 배치
        TokenDto tokenDto = authService.oauthenticate(newUser.getEmail());
        oauthService.saveRefreshToken(newUser, tokenDto);

        HttpHeaders headers = oauthService.setTokenHeaders(tokenDto);

        // 응답 작성
        SignupResponseDto responseDto = new SignupResponseDto();
        responseDto.setUser(authService.findUser(requestDto.getUser().getEmail()));
        responseDto.setResult("회원가입이 완료되었습니다.");
        return ResponseEntity.ok().headers(headers).body(responseDto);
    }*/


    // 프론트 -> 서버 로 인가코드 전송하면 받아오는 URI
    // 임시 URI 경로
    // https://kauth.kakao.com/oauth/authorize?client_id=4ab7b193bdd041a30446ede285a1f77a&redirect_uri=http://localhost:8080/api/authorization_code&response_type=code
    @GetMapping("/api/authorization_code")  // redirect uri로 code받아오고, access_token을 받아온다
    public ResponseEntity<TokenDto> getLogin(@RequestParam("code") String code) {

        // 인가코드를 넘겨주고 카카오 서버에게 액세스 토큰 발급 요청
        KakaoTokenDto kakaoTokenDto = oauthService.getKakaoAccessToken(code);

        // 액세스 토큰 발급 완료

        // 발급받은 액세스 토큰으로 카카오 서버에 회원정보 요청 후 DB에 저장
        TokenDto jwtTokenDto = oauthService.saveKakaoUser(kakaoTokenDto.getAccess_token());
        System.out.println("Kakao JWT Token 발급 완료 (OauthController) : " + jwtTokenDto.getAccessToken() + " \nRefreshToken : " + jwtTokenDto.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwtTokenDto.getAccessToken());

        return ResponseEntity.ok().headers(headers).body(jwtTokenDto);
    }

    @GetMapping("/api/code")
    public Response code(@RequestParam("code") String code) {
        String message = "성공적으로 카카오 유저 토큰 발급이 완료되었습니다.";
        return Response.success(message);
    }

    // 인가코드 과정 없이 바로 맥세스 토큰 받아오기
    public ResponseEntity<TokenDto> getKakaoAccessToken(@RequestParam("token") String token) {
        TokenDto jwtTokenDto = oauthService.saveKakaoUser(token);

        HttpHeaders headers = new HttpHeaders();
        headers.add(JwtFilter.AUTHORIZATION_HEADER, "Bearer " + jwtTokenDto.getAccessToken());

        return ResponseEntity.ok().headers(headers).body(jwtTokenDto);
    }
}
// Response 커스텀해서 반환형식 일치시킴
