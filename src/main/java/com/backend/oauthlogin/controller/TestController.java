package com.backend.oauthlogin.controller;

import com.backend.oauthlogin.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/api")
public class TestController {

    @GetMapping("/hello")
    public Response hello() {
        return Response.success();
    }

}
