package com.backend.oauthlogin.jwt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class AuthTokenProvider {

    private String expiry;

}
