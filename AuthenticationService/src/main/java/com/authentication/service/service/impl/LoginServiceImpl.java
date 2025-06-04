package com.authentication.service.service.impl;

import com.authentication.service.feignClient.UserManagementClients;
import com.authentication.service.request.LoginRequestParam;
import com.authentication.service.security.TokenManager;
import com.authentication.service.service.LoginService;
import com.common.exception.entity.authentication.LoginException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final UserManagementClients userManagementClients;
    private final StringRedisTemplate stringRedisTemplate;
    private final TokenManager tokenManager;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String studentLogin(LoginRequestParam loginRequestParam, String loginId) throws LoginException {
        try {
            if (loginId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "登录签证非法"
                ));
            }
            Student studentByStudentId = userManagementClients.getStudentByStudentId(loginRequestParam.getUsername());
            if (studentByStudentId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "用户不存在"
                ));
            }
            if (!studentByStudentId.getPassword().equals(Encryption.encryptToMd5(loginRequestParam.getPassword()))) {
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
            String userToken = tokenManager.createToken(studentByStudentId.getStudentId(), studentByStudentId.getStudentName(),
                    studentByStudentId.getRoleId());
            stringRedisTemplate.opsForValue().set(redisLoginKey + studentByStudentId.getStudentId(), userToken, 1, TimeUnit.DAYS);
            stringRedisTemplate.delete(loginId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("loginType", 1);
            resultMap.put("token", userToken);
            resultMap.put("id", studentByStudentId.getStudentId());
            resultMap.put("name", studentByStudentId.getStudentName());
            resultMap.put("roleId", studentByStudentId.getRoleId());
            resultMap.put("icon", studentByStudentId.getStudentIcon());
            resultMap.put("className", studentByStudentId.getStudentClass());
            resultMap.put("college", studentByStudentId.getCollege());
            return JsonSerialization.toJson(new BaseResponse<Map<String, Object>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, resultMap
            ));
        } catch (Exception exception) {
            throw new LoginException(exception.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String teacherLogin(LoginRequestParam loginRequestParam, String loginId) throws LoginException {
        try {
            if (loginId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "登录签证非法"
                ));
            }
            Teacher teacherByTeacherId = userManagementClients.getTeacherByTeacherId(loginRequestParam.getUsername());
            if (teacherByTeacherId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "用户不存在"
                ));
            }
            if (!teacherByTeacherId.getPassword().equals(Encryption.encryptToMd5(loginRequestParam.getPassword()))) {
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
            String userToken = tokenManager.createToken(teacherByTeacherId.getTeacherId(), teacherByTeacherId.getTeacherName(),
                    teacherByTeacherId.getTeacherJob());
            stringRedisTemplate.opsForValue().set(redisLoginKey + teacherByTeacherId.getTeacherId(), userToken, 1, TimeUnit.DAYS);
            stringRedisTemplate.delete(loginId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("loginType", 2);
            resultMap.put("token", userToken);
            resultMap.put("id", teacherByTeacherId.getTeacherId());
            resultMap.put("name", teacherByTeacherId.getTeacherName());
            resultMap.put("icon", teacherByTeacherId.getTeacherIcon());
            resultMap.put("job", teacherByTeacherId.getTeacherJob());
            resultMap.put("college", teacherByTeacherId.getTeacherCollege());
            return JsonSerialization.toJson(new BaseResponse<Map<String, Object>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, resultMap
            ));
        } catch (Exception exception) {
            throw new LoginException(exception.getMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String adminLogin(LoginRequestParam loginRequestParam, String loginId) throws LoginException {
        try{
            if (loginId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "登录签证非法"
                ));
            }
            Student studentByStudentId = userManagementClients.getAdminByAdminId(loginRequestParam.getUsername());
            if (studentByStudentId == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.FORBIDDEN_CODE, BaseResponseUtil.FORBIDDEN_MESSAGE, "用户不存在"
                ));
            }
            if (!studentByStudentId.getPassword().equals(Encryption.encryptToMd5(loginRequestParam.getPassword()))) {
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
            String userToken = tokenManager.createToken(studentByStudentId.getStudentId(), studentByStudentId.getStudentName(),
                    studentByStudentId.getRoleId());
            stringRedisTemplate.opsForValue().set(redisLoginKey + studentByStudentId.getStudentId(), userToken, 1, TimeUnit.DAYS);
            stringRedisTemplate.delete(loginId);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("loginType", 3);
            resultMap.put("token", userToken);
            resultMap.put("id", studentByStudentId.getStudentId());
            resultMap.put("name", studentByStudentId.getStudentName());
            resultMap.put("roleId", studentByStudentId.getRoleId());
            resultMap.put("icon", studentByStudentId.getStudentIcon());
            resultMap.put("className", studentByStudentId.getStudentClass());
            resultMap.put("college", studentByStudentId.getCollege());
            return JsonSerialization.toJson(new BaseResponse<Map<String, Object>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, resultMap
            ));
        }catch (Exception exception){
            throw new LoginException(exception.getMessage());
        }
    }
}
