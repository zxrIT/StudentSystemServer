package com.authentication.service.service;

import com.authentication.service.request.LoginRequestParam;

public interface LoginService {
    String studentLogin(LoginRequestParam loginRequestParam,String loginId);
    String teacherLogin(LoginRequestParam loginRequestParam,String loginId);
}
