package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.DeleteClassNameException;
import com.common.exception.entity.user.SelectClassNameException;
import com.common.exception.entity.user.UpdateClassNameException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.repository.ClassNameRepository;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.request.UpdateClassParam;
import com.user.management.service.service.ClassNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class ClassNameServiceImpl extends ServiceImpl<ClassNameRepository, ClassNameEntity>
        implements ClassNameService {
    private final ClassNameRepository classNameRepository;
    private final CollegeRepository collegeRepository;

    @Override
    public String getClassNames() throws SelectClassNameException {
        try {
            List<ClassNameEntity> classNameEntities
                    = classNameRepository.selectList(new LambdaQueryWrapper<ClassNameEntity>());
            classNameEntities.stream().forEach(className -> {
                CollegeEntity collegeEntity =
                        collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, className.getCollege()));
                className.setCollege(collegeEntity.getCollegeName());
            });
            return JsonSerialization.toJson(new BaseResponse<List<ClassNameEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, classNameEntities
            ));
        } catch (Exception exception) {
            throw new SelectClassNameException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateClassName(UpdateClassParam updateClassParam) throws UpdateClassNameException {
        try {
            ClassNameEntity classNameEntity =
                    classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>()
                            .eq(ClassNameEntity::getClassId, updateClassParam.getClassId()));
            if (classNameEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "班级不存在"
                ));
            }

            CollegeEntity collegeEntity
                    = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>()
                    .eq(CollegeEntity::getCollegeName, updateClassParam.getCollege()));
            if (collegeEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            classNameEntity.setCollege(collegeEntity.getCollegeId());
            classNameEntity.setClassName(updateClassParam.getClassName());
            classNameEntity.setCounselor(updateClassParam.getCounselor());
            classNameEntity.setHeadTeacher(updateClassParam.getHeadTeacher());
            classNameRepository.update(classNameEntity, new LambdaUpdateWrapper<ClassNameEntity>().eq(ClassNameEntity::getClassId,
                    updateClassParam.getClassId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功有部分数据未变下次登录后刷新"
            ));
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new UpdateClassNameException(exception.getMessage());
        }
    }

    @Override
    public String getClassNameByCollege(String collegeName) {
        try {
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeName, collegeName
            ));
            if (collegeEntity == null) {
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "院校不存在"
                ));
            }
            List<ClassNameEntity> classNameEntities =
                    classNameRepository.selectList(new LambdaQueryWrapper<ClassNameEntity>().eq(
                            ClassNameEntity::getCollege, collegeEntity.getCollegeId()
                    ));
            return JsonSerialization.toJson(new BaseResponse<List<ClassNameEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, classNameEntities
            ));
        } catch (Exception exception) {
            throw new SelectClassNameException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String deleteClassName(String classId) throws DeleteClassNameException {
        try {
            ClassNameEntity classNameEntity =
                    classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>()
                            .eq(ClassNameEntity::getClassId, classId));
            if (classNameEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "班级不存在"
                ));
            }
            classNameRepository.delete(new LambdaQueryWrapper<ClassNameEntity>()
                    .eq(ClassNameEntity::getClassId, classId));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "删除成功"
            ));
        } catch (Exception exception) {
            throw new DeleteClassNameException(exception.getMessage());
        }
    }
}
