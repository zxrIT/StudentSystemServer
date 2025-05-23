package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.DeleteStudentException;
import com.common.exception.entity.user.SelectStudentException;
import com.common.exception.entity.user.UpdateStudentException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.StudentEntity;
import com.user.management.service.repository.StudentRepository;
import com.user.management.service.request.ResetPasswordParam;
import com.user.management.service.request.UpdateStudentParam;
import com.user.management.service.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class StudentServiceImpl extends ServiceImpl<StudentRepository, StudentEntity> implements StudentService {
    @Autowired
    private final StudentRepository studentRepository;

    @Override
    public String getStudent(Integer quantity, Integer pages) {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<StudentEntity> page = new Page<>(pages, quantity);
            Page<StudentEntity> studentEntityPage = studentRepository.selectStudentPage(page);
            return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }

    @Override
    public String getSelectStudent(Integer payload, String content, Integer quantity, Integer pages) {
        try {
            if (quantity <= 0 || pages <= 0 || payload == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE,
                        "请求参数错误"
                ));
            }
            Page<StudentEntity> page = new Page<>(pages, quantity);
            switch (payload) {
                case 1:
                    Page<StudentEntity> studentEntityPageClass = studentRepository.selectStudentsByClass(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageClass
                    ));
                case 2:
                    Page<StudentEntity> studentEntityPageGrade = studentRepository.selectStudentsByGrade(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageGrade
                    ));
                case 3:
                    Page<StudentEntity> studentEntityPageId = studentRepository.selectStudentById(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageId
                    ));
                case 4:
                    Page<StudentEntity> studentEntityPageName = studentRepository.selectStudentsByName(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageName
                    ));
                default:
                    Page<StudentEntity> studentEntityPage = studentRepository.selectStudentPage(page);
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
                    ));
            }
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional
    public String resetPassword(ResetPasswordParam resetPasswordParam) {
        StudentEntity studentEntity = studentRepository.selectOne(new LambdaQueryWrapper<StudentEntity>().
                eq(StudentEntity::getStudentId,
                        resetPasswordParam.getStudentId()));
        if (studentEntity == null) {
            log.error("学生信息不存在");
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学生不存在"
            ));
        }
        studentEntity.setPassword(Encryption.encryptToMd5(studentEntity.getStudentId()));
        studentRepository.update(studentEntity, new LambdaQueryWrapper<StudentEntity>().
                eq(StudentEntity::getStudentId,
                        resetPasswordParam.getStudentId()));
        return JsonSerialization.toJson(new BaseResponse<String>(
                BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "重置密码成功"
        ));
    }

    @Override
    @DSTransactional
    public String updateStudent(UpdateStudentParam updateStudentParam) {
        try {
            StudentEntity studentEntity = studentRepository.selectOne(
                    new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getStudentId
                            , updateStudentParam.getStudentId()));
            if (studentEntity == null) {
                log.error("学生信息不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学生不存在"
                ));
            }
            studentEntity.setStudentName(updateStudentParam.getStudentName());
            studentEntity.setRoleId(updateStudentParam.getRoleId());
            studentEntity.setStudentGrade(updateStudentParam.getStudentGrade());
            studentEntity.setStudentSex(updateStudentParam.getStudentSex());
            studentEntity.setStudentClass(updateStudentParam.getStudentClass());
            studentRepository.update(studentEntity, new LambdaQueryWrapper<StudentEntity>().
                    eq(StudentEntity::getStudentId,
                            updateStudentParam.getStudentId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改信息成功"
            ));
        } catch (Exception exception) {
            throw new UpdateStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional
    public String deleteStudent(String studentId) {
        try {
            StudentEntity studentEntity = studentRepository.selectOne(
                    new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getStudentId,
                            studentId));
            if (studentEntity == null) {
                log.error("学生信息不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学生不存在"
                ));
            }
            studentRepository.delete(new LambdaQueryWrapper<StudentEntity>().eq(
                    StudentEntity::getStudentId, studentId
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "删除成功"
            ));
        } catch (Exception exception) {
            throw new DeleteStudentException(exception.getMessage());
        }
    }

    @Override
    public String getStudentByClass(Integer quantity, Integer pages, String className) {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<StudentEntity> page = new Page<>(pages, quantity);
            Page<StudentEntity> studentEntityPage = studentRepository.selectStudentsBasicInfoByClass(page, className);
            return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }
}
