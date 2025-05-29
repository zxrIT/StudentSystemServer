package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.SelectTeacherException;
import com.common.exception.entity.user.UpdateTeacherException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.Encryption;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.entity.TeacherEntity;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.repository.TeacherEntityRepository;
import com.user.management.service.request.UpdateTeacherParam;
import com.user.management.service.service.TeacherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class TeacherServiceImpl extends ServiceImpl<TeacherEntityRepository, TeacherEntity>
        implements TeacherService {
    private final TeacherEntityRepository teacherEntityRepository;
    private final CollegeRepository collegeRepository;

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
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(CollegeEntity::getCollegeName,
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
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageCollege
                    ));
                case 2:
                    Page<TeacherEntity> teacherEntityPageId = teacherEntityRepository.selectTeacherById(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageId
                    ));
                case 3:
                    Page<TeacherEntity> teacherEntityPageName = teacherEntityRepository.selectTeacherByName(page, content);
                    return JsonSerialization.toJson(new BaseResponse<Page<TeacherEntity>>(
                            BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntityPageName
                    ));
                default:
                    Page<TeacherEntity> teacherEntityPage = teacherEntityRepository.selectTeacherPage(page);
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
            return JsonSerialization.toJson(new BaseResponse<List<TeacherEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, teacherEntities
            ));
        } catch (Exception exception) {
            throw new SelectTeacherException(exception.getMessage());
        }
    }
}
