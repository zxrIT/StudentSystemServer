package com.authentication.service.service.impl;

import com.authentication.service.entity.StudentAuth;
import com.authentication.service.entity.TeacherAuth;
import com.authentication.service.repository.StudentAuthRepository;
import com.authentication.service.repository.TeacherAuthRepository;
import com.authentication.service.request.LoginRequestParam;
import com.authentication.service.security.TokenManager;
import com.authentication.service.service.LoginService;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.common.exception.entity.authentication.LoginException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class LoginServiceImpl implements LoginService {
    private final static String redisLoginKey = "student:token:";

    @Autowired
    private final StudentAuthRepository studentAuthRepository;
    private final TeacherAuthRepository teacherAuthRepository;
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenManager tokenManager;

    @Override
    @DSTransactional
    public String studentLogin(LoginRequestParam loginRequestParam, String loginId) {
        try {
            if (loginId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "登录签证非法"
                ));
            }
            StudentAuth studentAuth =
                    studentAuthRepository.selectOne(new LambdaQueryWrapper<StudentAuth>().eq(StudentAuth::getStudentId,
                            loginRequestParam.getUsername()));
            if (studentAuth == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "用户不存在"
                ));
            }
            if (!studentAuth.getPassword().equals(Encryption.encryptToMd5(loginRequestParam.getPassword()))) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "密码错误请重新输入"
                ));
            }
            String redisCode = stringRedisTemplate.opsForValue().get(loginId);
            if (redisCode == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "验证码过期或不存在"
                ));
            }
            if (!redisCode.toLowerCase().equals(loginRequestParam.getCode().toLowerCase())) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "验证码错误"
                ));
            }
            String userToken = tokenManager.createToken(studentAuth.getStudentId(), studentAuth.getStudentName(),
                    studentAuth.getRoleId());
            stringRedisTemplate.opsForValue().set(redisLoginKey + studentAuth.getStudentId(), userToken, 1, TimeUnit.DAYS);
            stringRedisTemplate.delete(loginId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", userToken);
            resultMap.put("id", studentAuth.getStudentId());
            resultMap.put("name", studentAuth.getStudentName());
            resultMap.put("roleId", studentAuth.getRoleId());
            resultMap.put("icon", studentAuth.getStudentIcon());
            resultMap.put("className", studentAuth.getStudentClass());
            return JsonSerialization.toJson(new BaseResponse<Map<String, Object>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, resultMap
            ));
        } catch (Exception exception) {
            throw new LoginException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional
    public String teacherLogin(LoginRequestParam loginRequestParam, String loginId) {
        try {
            if (loginId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "登录签证非法"
                ));
            }
            TeacherAuth teacherAuth = teacherAuthRepository.selectOne(new LambdaQueryWrapper<TeacherAuth>().eq(TeacherAuth::getTeacherId,
                    loginRequestParam.getUsername()));
            if (teacherAuth == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "用户不存在"
                ));
            }
            if (!teacherAuth.getPassword().equals(Encryption.encryptToMd5(loginRequestParam.getPassword()))) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "密码错误请重新输入"
                ));
            }
            String redisCode = stringRedisTemplate.opsForValue().get(loginId);
            if (redisCode == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "验证码过期或不存在"
                ));
            }
            if (!redisCode.toLowerCase().equals(loginRequestParam.getCode().toLowerCase())) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "验证码错误"
                ));
            }
            String userToken = tokenManager.createToken(teacherAuth.getTeacherId(), teacherAuth.getTeacherName(),
                    teacherAuth.getTeacherJob());
            stringRedisTemplate.opsForValue().set(redisLoginKey + teacherAuth.getTeacherId(), userToken, 1, TimeUnit.DAYS);
            stringRedisTemplate.delete(loginId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("token", userToken);
            resultMap.put("id", teacherAuth.getTeacherId());
            resultMap.put("name", teacherAuth.getTeacherName());
            resultMap.put("icon", teacherAuth.getTeacherIcon());
            resultMap.put("job", teacherAuth.getTeacherJob());
            resultMap.put("college", teacherAuth.getTeacherCollege());
            return JsonSerialization.toJson(new BaseResponse<Map<String, Object>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, resultMap
            ));
        } catch (Exception exception) {
            throw new LoginException(exception.getMessage());
        }
    }
}
