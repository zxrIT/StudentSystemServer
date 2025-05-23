package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.DeleteClassNameException;
import com.common.exception.entity.user.SelectClassNameException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.ClassNameEntity;
import com.user.management.service.repository.ClassNameRepository;
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

    @Override
    public String getClassNames() {
        try {
            List<ClassNameEntity> classNameEntities
                    = classNameRepository.selectList(new LambdaQueryWrapper<ClassNameEntity>());
            return JsonSerialization.toJson(new BaseResponse<List<ClassNameEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, classNameEntities
            ));
        } catch (Exception exception) {
            throw new SelectClassNameException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional
    public String deleteClassName(String classId) {
        try {
            ClassNameEntity classNameEntity =
                    classNameRepository.selectOne(new LambdaQueryWrapper<ClassNameEntity>()
                            .eq(ClassNameEntity::getClassId, classId));
            if (classNameEntity == null) {
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
