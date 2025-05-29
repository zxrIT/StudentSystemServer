package com.authentication.service.controller;

import com.authentication.service.request.LoginRequestParam;
import com.authentication.service.security.TokenManager;
import com.authentication.service.service.LoginService;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/authentication")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "认证", description = "认证相关操作API")
public class AuthenticationController {
    private final static String LOGIN_ID = "loginId";
    @Autowired
    private final LoginService loginService;
    private final TokenManager tokenManager;

    @Operation(summary = "学生登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "403", description = "登录失败"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/student/login")
    public String studentLogin(@RequestBody LoginRequestParam loginRequestParam, HttpServletRequest request) {
        String loginId = request.getHeader(LOGIN_ID);
        return loginService.studentLogin(loginRequestParam, loginId);
    }

    @Operation(summary = "教师登录")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "登录成功"),
            @ApiResponse(responseCode = "403", description = "登录失败"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/teacher/login")
    public String teacherLogin(@RequestBody LoginRequestParam loginRequestParam, HttpServletRequest request) {
        String loginId = request.getHeader(LOGIN_ID);
        return loginService.teacherLogin(loginRequestParam, loginId);
    }

    @Operation(summary = "验证token合法性")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "验证成功"),
            @ApiResponse(responseCode = "403", description = "验证失败"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/verification/token")
    public String verificationToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        if (tokenManager.validateToken(token)) {
            return JsonSerialization.toJson(new BaseResponse<Boolean>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, true
            ));
        }
        return JsonSerialization.toJson(new BaseResponse<Boolean>(
                BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, false
        ));
    }
}
