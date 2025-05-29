package com.authentication.service.request;

import lombok.Data;

@Data
public class LoginRequestParam {
    private String username;
    private String password;
    private String code;
}
