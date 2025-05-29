package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.*;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.entity.StudentEntity;
import com.user.management.service.repository.ClassNameRepository;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.repository.StudentRepository;
import com.user.management.service.request.IncrementStudentParam;
import com.user.management.service.request.ResetPasswordParam;
import com.user.management.service.request.UpdateStudentParam;
import com.user.management.service.service.StudentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class StudentServiceImpl extends ServiceImpl<StudentRepository, StudentEntity> implements StudentService {
    private static final String UPLOAD_DIR = "/Users/zengxiangrui/DistributedProject/StudentSystem/static";

    @Autowired
    private final StudentRepository studentRepository;
    private final ClassNameRepository classNameRepository;
    private final CollegeRepository collegeRepository;

    @Override
    public String getStudent(Integer quantity, Integer pages) throws SelectStudentException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<StudentEntity> page = new Page<>(pages, quantity);
            Page<StudentEntity> studentEntityPage = studentRepository.selectStudentPage(page);
            studentEntityPage.getRecords().forEach(studentEntity -> {
                ClassNameEntity classNameEntity =
                        classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, studentEntity.getCollege()
                ));
                studentEntity.setStudentClass(classNameEntity.getClassName());
                studentEntity.setCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String incrementStudent(IncrementStudentParam incrementStudentParam,
                                   MultipartFile iconImage) throws IncrementStudentException,
            UploadStudentImageException {
        try {
            if (studentRepository.exists(new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getStudentId,
                    incrementStudentParam.getStudentId()))) {
                log.error("学号已存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学号已存在"
                ));
            }
            String fileUrl = UUID.randomUUID().toString() + "_" +
                    System.currentTimeMillis() + "_" + iconImage.getOriginalFilename();
            String fileName = "http://localhost:10000/" + fileUrl;
            StudentEntity studentEntity = new StudentEntity();
            studentEntity.setStudentId(incrementStudentParam.getStudentId());
            studentEntity.setStudentSex(incrementStudentParam.getStudentSex());
            studentEntity.setStudentName(incrementStudentParam.getStudentName());
            studentEntity.setRoleId(incrementStudentParam.getRoleId());
            studentEntity.setStudentGrade(incrementStudentParam.getStudentGrade());
            studentEntity.setStudentIcon(fileName);
            studentEntity.setId(UUID.randomUUID().toString() + System.currentTimeMillis());
            ClassNameEntity classNameEntity =
                    classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>()
                            .eq(ClassNameEntity::getClassName, incrementStudentParam.getStudentClass()));
            if (classNameEntity == null) {
                log.error("班级不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "班级不存在"
                ));
            }
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>()
                    .eq(CollegeEntity::getCollegeName, incrementStudentParam.getCollege()));
            if (collegeEntity == null) {
                log.error("学院不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            classNameEntity.setStudentCount(classNameEntity.getStudentCount() + 1);
            studentEntity.setStudentClass(classNameEntity.getClassId());
            studentEntity.setCollege(collegeEntity.getCollegeId());
            studentRepository.insert(studentEntity);
            classNameRepository.update(classNameEntity, new LambdaUpdateWrapper<ClassNameEntity>().eq(
                    ClassNameEntity::getClassId, classNameEntity.getClassId()
            ));
            try {
                File destFile = new File(UPLOAD_DIR + File.separator + fileUrl);
                iconImage.transferTo(destFile);
            } catch (Exception exception) {
                throw new UploadStudentImageException(exception.getMessage());
            }
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new IncrementStudentException(exception.getMessage());
        }
    }

    @Override
    public String getSelectStudent(Integer payload, String content, Integer quantity, Integer pages) throws SelectStudentException {
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
                    studentEntityPageClass.getRecords().stream().forEach(studentEntity -> {
                        ClassNameEntity classNameEntity =
                                classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                        eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, studentEntity.getCollege()
                        ));
                        studentEntity.setStudentClass(classNameEntity.getClassName());
                        studentEntity.setCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageClass
                    ));
                case 2:
                    Page<StudentEntity> studentEntityPageGrade = studentRepository.selectStudentsByGrade(page, content);
                    studentEntityPageGrade.getRecords().stream().forEach(studentEntity -> {
                        ClassNameEntity classNameEntity =
                                classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                        eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, studentEntity.getCollege()
                        ));
                        studentEntity.setStudentClass(classNameEntity.getClassName());
                        studentEntity.setCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageGrade
                    ));
                case 3:
                    Page<StudentEntity> studentEntityPageId = studentRepository.selectStudentById(page, content);
                    studentEntityPageId.getRecords().stream().forEach(studentEntity -> {
                        ClassNameEntity classNameEntity =
                                classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                        eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, studentEntity.getCollege()
                        ));
                        studentEntity.setStudentClass(classNameEntity.getClassName());
                        studentEntity.setCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageId
                    ));
                case 4:
                    Page<StudentEntity> studentEntityPageName = studentRepository.selectStudentsByName(page, content);
                    studentEntityPageName.getRecords().stream().forEach(studentEntity -> {
                        ClassNameEntity classNameEntity =
                                classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                        eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, studentEntity.getCollege()
                        ));
                        studentEntity.setStudentClass(classNameEntity.getClassName());
                        studentEntity.setCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPageName
                    ));
                default:
                    Page<StudentEntity> studentEntityPage = studentRepository.selectStudentPage(page);
                    studentEntityPage.getRecords().stream().forEach(studentEntity -> {
                        ClassNameEntity classNameEntity =
                                classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                        eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, studentEntity.getCollege()
                        ));
                        studentEntity.setStudentClass(classNameEntity.getClassName());
                        studentEntity.setCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
                    ));
            }
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }

    @Override
    public String getStudentByClass(Integer quantity, Integer pages, String className) throws SelectStudentException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<StudentEntity> page = new Page<>(pages, quantity);
            Page<StudentEntity> studentEntityPage = studentRepository.selectStudentsBasicInfoByClass(page, className);
            studentEntityPage.getRecords().stream().forEach(studentEntity -> {
                ClassNameEntity classNameEntity =
                        classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().
                                eq(ClassNameEntity::getClassId, studentEntity.getStudentClass()));
                CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, studentEntity.getCollege()
                ));
                studentEntity.setStudentClass(classNameEntity.getClassName());
                studentEntity.setCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<Page<StudentEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, studentEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String resetPassword(ResetPasswordParam resetPasswordParam) throws UpdateStudentException {
        try {
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
        } catch (Exception exception) {
            throw new UpdateStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateStudent(UpdateStudentParam updateStudentParam) throws UpdateStudentException {
        try {
            StudentEntity studentEntity = studentRepository.selectOne(
                    new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getId
                            , updateStudentParam.getId()));
            if (studentEntity == null) {
                log.error("学生信息不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学生不存在"
                ));
            }
            if (studentRepository.exists(new LambdaQueryWrapper<StudentEntity>().eq(StudentEntity::getStudentId,
                    updateStudentParam.getStudentId()).ne(StudentEntity::getId, updateStudentParam.getId()))) {
                log.error("学号已存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学号已存在"
                ));
            }
            studentEntity.setStudentId(updateStudentParam.getStudentId());
            studentEntity.setStudentName(updateStudentParam.getStudentName());
            studentEntity.setRoleId(updateStudentParam.getRoleId());
            studentEntity.setStudentGrade(updateStudentParam.getStudentGrade());
            studentEntity.setStudentSex(updateStudentParam.getStudentSex());
            ClassNameEntity classNameEntity =
                    classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>().eq(ClassNameEntity::getClassName,
                            updateStudentParam.getStudentClass()));
            studentEntity.setStudentClass(classNameEntity.getClassId());
            if (classNameEntity == null) {
                log.error("班级不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "班级不存在"
                ));
            }
            CollegeEntity collegeEntity =
                    collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(CollegeEntity::getCollegeName,
                            updateStudentParam.getCollege()));
            studentEntity.setCollege(collegeEntity.getCollegeId());
            if (collegeEntity == null) {
                log.error("学院不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            studentRepository.update(studentEntity, new LambdaQueryWrapper<StudentEntity>().
                    eq(StudentEntity::getId,
                            updateStudentParam.getId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功有部分数据未变下次登录后刷新"
            ));
        } catch (Exception exception) {
            throw new UpdateStudentException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String deleteStudent(String studentId) throws DeleteStudentException {
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
}
