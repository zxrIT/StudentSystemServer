package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.DeleteClassNameException;
import com.common.exception.entity.user.IncrementClassException;
import com.common.exception.entity.user.SelectClassNameException;
import com.common.exception.entity.user.UpdateClassNameException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.entity.TeacherEntity;
import com.user.management.service.repository.ClassNameRepository;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.repository.TeacherEntityRepository;
import com.user.management.service.request.IncrementClassParam;
import com.user.management.service.request.UpdateClassParam;
import com.user.management.service.service.ClassNameService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class ClassNameServiceImpl extends ServiceImpl<ClassNameRepository, ClassNameEntity>
        implements ClassNameService {
    private final TeacherEntityRepository teacherEntityRepository;
    private final ClassNameRepository classNameRepository;
    private final CollegeRepository collegeRepository;

    @Override
    public String getClassNames() throws SelectClassNameException {
        try {
            List<ClassNameEntity> classNameEntities
                    = classNameRepository.selectList(new LambdaQueryWrapper<ClassNameEntity>()
                    .orderByAsc(ClassNameEntity::getClassName));
            classNameEntities.stream().forEach(className -> {
                CollegeEntity collegeEntity =
                        collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                                CollegeEntity::getCollegeId, className.getCollege()));
                className.setCollege(collegeEntity.getCollegeName());
                TeacherEntity teacherEntity =
                        teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                                TeacherEntity::getTeacherId, className.getHeadTeacher()
                        ));
                if (teacherEntity == null) {
                    className.setHeadTeacher(null);
                } else {
                    className.setHeadTeacher(teacherEntity.getTeacherName());
                }
                TeacherEntity teacherEntityCounselor =
                        teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                                TeacherEntity::getTeacherId, className.getCounselor()
                        ).eq(TeacherEntity::getIsCounselor, true));
                if (teacherEntityCounselor == null) {
                    className.setCounselor(null);
                } else {
                    className.setCounselor(teacherEntityCounselor.getTeacherName());
                }
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
    public String incrementClass(IncrementClassParam incrementClassParam) throws IncrementClassException {
        try {
            ClassNameEntity classNameEntityNew = new ClassNameEntity();
            if (classNameRepository.exists(new LambdaQueryWrapper<ClassNameEntity>().eq(ClassNameEntity::getClassName,
                    incrementClassParam.getClassName()))) {
                log.error("请求参数错误,班级名已存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "班级名已存在"
                ));
            }
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeName, incrementClassParam.getCollege()
            ));
            if (collegeEntity == null) {
                log.error("请求参数错误,学院不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "学院不存在"
                ));
            }
            TeacherEntity teacherEntityTeacher = teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherName, incrementClassParam.getHeadTeacher()
            ));
            if (teacherEntityTeacher == null) {
                log.error("请求参数错误,教师不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "教师不存在"
                ));
            }
            TeacherEntity teacherEntityCounselor = teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                    TeacherEntity::getTeacherName, incrementClassParam.getCounselor()
            ).eq(TeacherEntity::getIsCounselor, true));
            if (teacherEntityCounselor == null) {
                log.error("请求参数错误,辅导员不存在");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "辅导员不存在"
                ));
            }
            collegeEntity.setClassCount(collegeEntity.getClassCount() + 1);
            classNameEntityNew.setClassName(incrementClassParam.getClassName());
            classNameEntityNew.setStudentCount(incrementClassParam.getStudentCount());
            classNameEntityNew.setCollege(collegeEntity.getCollegeId());
            classNameEntityNew.setClassId(UUID.randomUUID().toString() + System.currentTimeMillis());
            classNameEntityNew.setHeadTeacher(teacherEntityTeacher.getTeacherId());
            classNameEntityNew.setCounselor(teacherEntityCounselor.getTeacherId());
            classNameRepository.insert(classNameEntityNew);
            collegeRepository.update(collegeEntity, new LambdaUpdateWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeName, incrementClassParam.getCollege()
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new IncrementClassException(exception.getMessage());
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
            if (!classNameEntity.getCollege().equals(collegeEntity.getCollegeId())) {
                CollegeEntity collegeEntityOld = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, classNameEntity.getCollege()
                ));
                if (!(collegeEntityOld.getClassCount() == 0)) {
                    collegeEntityOld.setClassCount(collegeEntityOld.getClassCount() - 1);
                }
                collegeEntity.setClassCount(collegeEntity.getClassCount() + 1);
                collegeRepository.update(collegeEntityOld, new LambdaUpdateWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, classNameEntity.getCollege()
                ));
                collegeRepository.update(collegeEntity, new LambdaUpdateWrapper<CollegeEntity>().eq(
                        CollegeEntity::getCollegeId, collegeEntity.getCollegeId()
                ));
                classNameEntity.setCollege(collegeEntity.getCollegeId());
            }
            classNameEntity.setClassName(updateClassParam.getClassName());
            TeacherEntity teacherEntity =
                    teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                            TeacherEntity::getTeacherName, updateClassParam.getHeadTeacher()
                    ));
            if (teacherEntity == null) {
                classNameEntity.setHeadTeacher(null);
            } else {
                classNameEntity.setHeadTeacher(teacherEntity.getTeacherId());
            }
            TeacherEntity teacherEntityCounselor =
                    teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                            TeacherEntity::getTeacherName, updateClassParam.getCounselor()
                    ).eq(TeacherEntity::getIsCounselor, true));
            if (teacherEntityCounselor == null) {
                classNameEntity.setCounselor(null);
            } else {
                classNameEntity.setCounselor(teacherEntityCounselor.getTeacherId());
            }
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
            classNameEntities.stream().forEach(className -> {
                TeacherEntity teacherEntity =
                        teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                                TeacherEntity::getTeacherId, className.getHeadTeacher()
                        ));
                if (teacherEntity == null) {
                    className.setHeadTeacher(null);
                } else {
                    className.setHeadTeacher(teacherEntity.getTeacherName());
                }
                TeacherEntity teacherEntityCounselor =
                        teacherEntityRepository.selectOne(new LambdaQueryWrapper<TeacherEntity>().eq(
                                TeacherEntity::getTeacherId, className.getCounselor()
                        ).eq(TeacherEntity::getIsCounselor, true));
                if (teacherEntityCounselor == null) {
                    className.setCounselor(null);
                } else {
                    className.setCounselor(teacherEntityCounselor.getTeacherName());
                }
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
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeId, classNameEntity.getCollege()
            ));
            collegeEntity.setClassCount(collegeEntity.getClassCount() - 1);
            collegeRepository.update(collegeEntity, new LambdaUpdateWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeId, collegeEntity.getCollegeId()
            ));
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
