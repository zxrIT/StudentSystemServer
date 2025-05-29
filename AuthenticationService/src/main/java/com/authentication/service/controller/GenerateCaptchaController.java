package com.authentication.service.controller;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.authentication.service.utils.RedisIdWorker;
import com.common.exception.entity.authentication.GenerateCaptchaException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/authentication")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "生成图片验证码", description = "生成图片验证码相关操作API")
public class GenerateCaptchaController {
    private final static String LOGIN_ID = "loginId";
    private final static Integer LOGIN_ID_Time = 1;
    private final static long maxAllowedSeconds = 300;

    @Autowired
    private final RedisIdWorker redisIdWorker;
    private final StringRedisTemplate stringRedisTemplate;

    @Operation(summary = "生成图片验证码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "生成成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/captcha")
    public String generateCaptcha(
            HttpServletRequest httpServletRequest,
            HttpServletResponse httpServletResponse) throws GenerateCaptchaException {
        try {
            String loginId = httpServletRequest.getHeader(LOGIN_ID);
            if (loginId == null || !redisIdWorker.isIdValidInRedis(Long.parseLong(loginId),
                    LOGIN_ID, maxAllowedSeconds) || loginId.equals("0")) {
                loginId = Long.toString(redisIdWorker.nextId(LOGIN_ID));
                log.info("redis生成新loginId:" + loginId);
            }
            LineCaptcha captcha = CaptchaUtil.createLineCaptcha(100, 40, 4, 40);
            String code = captcha.getCode();
            stringRedisTemplate.opsForValue().set(loginId, code, LOGIN_ID_Time, TimeUnit.MINUTES);
            Map<String, String> response = new HashMap<>();
            response.put("loginId", loginId);
            response.put("captchaBase64", "data:image/png;base64," + captcha.getImageBase64());
            return JsonSerialization.toJson(new BaseResponse<Map<String, String>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, response
            ));
        } catch (Exception exception) {
            throw new GenerateCaptchaException(exception.getMessage());
        }
    }
}
