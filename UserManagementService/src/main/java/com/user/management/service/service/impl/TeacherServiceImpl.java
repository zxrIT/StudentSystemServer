package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.*;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.entity.TeacherEntity;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.repository.TeacherEntityRepository;
import com.user.management.service.request.IncrementTeacherParam;
import com.user.management.service.request.UpdateTeacherParam;
import com.user.management.service.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class TeacherServiceImpl extends ServiceImpl<TeacherEntityRepository, TeacherEntity>
        implements TeacherService {
    private static final String UPLOAD_DIR = "/Users/zengxiangrui/DistributedProject/StudentSystem/static";

    private final TeacherEntityRepository teacherEntityRepository;
    private final CollegeRepository collegeRepository;

    @Override
    @DSTransactional
    public String deleteTeacher(String teacherId) {
        try {
            if (!teacherEntityRepository.exists(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherId, teacherId
            ))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误,教师不存在"
                ));
            }
            teacherEntityRepository.delete(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherId, teacherId
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "删除成功"
            ));
        } catch (Exception exception) {
            throw new DeleteTeacherException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String incrementTeacher(IncrementTeacherParam incrementTeacherParam, MultipartFile iconImage)
            throws IncrementTeacherException, UploadTeacherImageException {
        try {
            if (teacherEntityRepository.exists(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherId, incrementTeacherParam.getTeacherId()
            ))) {
                log.error("教师序号已存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "教师序号已存在"
                ));
            }
            if (teacherEntityRepository.exists(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherName, incrementTeacherParam.getTeacherName()
            ))) {
                log.error("教师名已存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "教师名不能重复"
                ));
            }
            String fileUrl = UUID.randomUUID().toString() + "_" +
                    System.currentTimeMillis() + "_" + iconImage.getOriginalFilename();
            String fileName = "http://localhost:10000/" + fileUrl;
            TeacherEntity teacherEntity = new TeacherEntity();
            teacherEntity.setTeacherId(incrementTeacherParam.getTeacherId());
            teacherEntity.setTeacherName(incrementTeacherParam.getTeacherName());
            teacherEntity.setPassword(Encryption.encryptToMd5(incrementTeacherParam.getTeacherId()));
            teacherEntity.setId(UUID.randomUUID().toString() + System.currentTimeMillis());
            teacherEntity.setTeacherJob(incrementTeacherParam.getTeacherJob());
            teacherEntity.setIsCounselor(incrementTeacherParam.getIsCounselor());
            teacherEntity.setTeacherIcon(fileName);
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeName, incrementTeacherParam.getTeacherCollege()
            ));
            if (collegeEntity == null) {
                log.error("学院不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            teacherEntity.setTeacherCollege(collegeEntity.getCollegeId());
            teacherEntityRepository.insert(teacherEntity);
            try {
                File destFile = new File(UPLOAD_DIR + File.separator + fileUrl);
                iconImage.transferTo(destFile);
            } catch (Exception exception) {
                throw new UploadTeacherImageException(exception.getMessage());
            }
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }

    @Override
    public String getTeacher(Integer quantity, Integer pages) throws SelectTeacherException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<TeacherEntity> page = new Page<>(pages, quantity);
            Page<TeacherEntity> teacherEntityPage = teacherEntityRepository.selectTeacherPage(page);
            teacherEntityPage.getRecords().stream().forEach(teacherEntity -> {
                CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                ));
                teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateTeacher(UpdateTeacherParam updateTeacherParam) throws UpdateTeacherException {
        try {
            TeacherEntity teacherEntity =
                    teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>()
                            .eq(TeacherEntity::getId, updateTeacherParam.getId()));
            if (teacherEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，教师不存在"
                ));
            }
            if (teacherEntityRepository.exists(new LambdaQueryWrapper<TeacherEntity>().eq(
                            TeacherEntity::getTeacherId, updateTeacherParam.getTeacherId())
                    .ne(TeacherEntity::getId, updateTeacherParam.getId()))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，教师序号已存在"
                ));
            }
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>()
                    .eq(CollegeEntity::getCollegeName,
                            updateTeacherParam.getTeacherCollege()));
            if (collegeEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            teacherEntity.setTeacherId(updateTeacherParam.getTeacherId());
            teacherEntity.setTeacherCollege(collegeEntity.getCollegeId());
            teacherEntity.setTeacherName(updateTeacherParam.getTeacherName());
            teacherEntity.setTeacherJob(updateTeacherParam.getTeacherJob());
            teacherEntity.setIsCounselor(updateTeacherParam.getIsCounselor());
            teacherEntityRepository.update(teacherEntity, new LambdaQueryWrapper<TeacherEntity>().eq(TeacherEntity::getId,
                    updateTeacherParam.getId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功有部分数据未变下次登录后刷新"
            ));
        } catch (Exception exception) {
            throw new UpdateTeacherException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String resetPassword(String teacherId) throws UpdateTeacherException {
        try {
            TeacherEntity teacherEntity = teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>()
                    .eq(TeacherEntity::getTeacherId, teacherId));
            if (teacherEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，教师不存在"
                ));
            }
            teacherEntity.setPassword(Encryption.encryptToMd5(teacherEntity.getTeacherId()));
            teacherEntityRepository.update(teacherEntity, new LambdaQueryWrapper<TeacherEntity>()
                    .eq(TeacherEntity::getTeacherId, teacherId));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "重置成功"
            ));
        } catch (Exception exception) {
            throw new UpdateTeacherException(exception.getMessage());
        }
    }

    @Override
    public String getSelectTeacher(Integer payload, String content, Integer quantity, Integer pages) throws SelectTeacherException {
        try {
            if (quantity <= 0 || pages <= 0 || payload == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE,
                        "请求参数错误"
                ));
            }
            Page<TeacherEntity> page = new Page<>(pages, quantity);
            switch (payload) {
                case 1:
                    Page<TeacherEntity> teacherEntityPageCollege = teacherEntityRepository.selectTeacherByCollege(page, content);
                    teacherEntityPageCollege.getRecords().stream().forEach(teacherEntity -> {
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                        ));
                        teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageCollege
                    ));
                case 2:
                    Page<TeacherEntity> teacherEntityPageId = teacherEntityRepository.selectTeacherById(page, content);
                    teacherEntityPageId.getRecords().stream().forEach(teacherEntity -> {
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                        ));
                        teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageId
                    ));
                case 3:
                    Page<TeacherEntity> teacherEntityPageName = teacherEntityRepository.selectTeacherByName(page, content);
                    teacherEntityPageName.getRecords().stream().forEach(teacherEntity -> {
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                        ));
                        teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageName
                    ));
                default:
                    Page<TeacherEntity> teacherEntityPage = teacherEntityRepository.selectTeacherPage(page);
                    teacherEntityPage.getRecords().stream().forEach(teacherEntity -> {
                        CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                        ));
                        teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
                    });
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPage
                    ));
            }
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }

    @Override
    public String getAllCounselor() throws SelectTeacherException {
        try {
            List<TeacherEntity> teacherEntities = teacherEntityRepository.selectAllCounselor();
            teacherEntities.stream().forEach(teacherEntity -> {
                CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                ));
                teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<List<TeacherEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntities
            ));
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }

    @Override
    public String getAllTeacher() throws SelectTeacherException {
        try {
            List<TeacherEntity> teacherEntities = teacherEntityRepository.selectAllTeacher();
            teacherEntities.stream().forEach(teacherEntity -> {
                CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, teacherEntity.getTeacherCollege()
                ));
                teacherEntity.setTeacherCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<List<TeacherEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntities
            ));
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }
}
