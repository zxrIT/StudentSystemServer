package com.curriculum.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.curriculum.IncrementCurriculumException;
import com.common.exception.entity.curriculum.MoveCurriculumException;
import com.common.exception.entity.curriculum.SelectCurriculumException;
import com.common.exception.entity.curriculum.UpdateCurriculumException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.curriculum.management.service.entity.CurriculumDeleteEntity;
import com.curriculum.management.service.entity.CurriculumEntity;
import com.curriculum.management.service.repository.CurriculumDeleteEntityRepository;
import com.curriculum.management.service.repository.CurriculumEntityRepository;
import com.curriculum.management.service.request.IncrementCurriculumParam;
import com.curriculum.management.service.request.UpdateCurriculumParam;
import com.curriculum.management.service.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CurriculumServiceImpl extends ServiceImpl<CurriculumEntityRepository, CurriculumEntity>
        implements CurriculumService {
    @Autowired
    private final CurriculumEntityRepository curriculumEntityRepository;
    private final CurriculumDeleteEntityRepository curriculumDeleteEntityRepository;

    @Override
    public String getCurriculum(Integer quantity, Integer pages) throws SelectCurriculumException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<CurriculumEntity> page = new Page<>(pages, quantity);
            Page<CurriculumEntity> curriculumEntityPage =
                    curriculumEntityRepository.selectPage(page, new LambdaQueryWrapper<CurriculumEntity>()
                            .orderByAsc(CurriculumEntity::getId));
            return JsonSerialization.toJson(new BaseResponse<Page<CurriculumEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, curriculumEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCurriculumException(exception.getMessage());
        }
    }

    @Override
    public String getCurriculumById(String courseId) throws SelectCurriculumException {
        try {
            CurriculumEntity curriculumEntity =
                    curriculumEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumEntity>(
                    ).eq(CurriculumEntity::getId, courseId));
            if (curriculumEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "获取课程失败请重试"
                ));
            }
            return JsonSerialization.toJson(new BaseResponse<CurriculumEntity>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, curriculumEntity
            ));
        } catch (Exception exception) {
            throw new SelectCurriculumException(exception.getMessage());
        }
    }

    @Override
    public String getCurriculumDelete(Integer quantity, Integer pages) throws SelectCurriculumException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<CurriculumDeleteEntity> page = new Page<>(pages, quantity);
            Page<CurriculumDeleteEntity> curriculumDeleteEntityPage =
                    curriculumDeleteEntityRepository.selectPage(page, new LambdaQueryWrapper<CurriculumDeleteEntity>()
                            .orderByAsc(CurriculumDeleteEntity::getId));
            return JsonSerialization.toJson(new BaseResponse<Page<CurriculumDeleteEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, curriculumDeleteEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCurriculumException(exception.getMessage());
        }
    }

    @Override
    public String getSelectCurriculum(String content, Integer quantity, Integer pages) throws SelectCurriculumException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<CurriculumEntity> page = new Page<>(pages, quantity);
            Page<CurriculumEntity> curriculumEntityPage =
                    curriculumEntityRepository.selectPage(page, new LambdaQueryWrapper<CurriculumEntity>()
                            .like(CurriculumEntity::getName, content)
                            .orderByAsc(CurriculumEntity::getId));
            return JsonSerialization.toJson(new BaseResponse<Page<CurriculumEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, curriculumEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCurriculumException(exception.getMessage());
        }
    }

    @Override
    public String getDeleteSelectCurriculum(String content, Integer quantity, Integer pages) throws SelectCurriculumException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<CurriculumDeleteEntity> page = new Page<>(pages, quantity);
            Page<CurriculumDeleteEntity> curriculumDeleteEntityPage =
                    curriculumDeleteEntityRepository.selectPage(page, new LambdaQueryWrapper<CurriculumDeleteEntity>()
                            .like(CurriculumDeleteEntity::getName, content)
                            .orderByAsc(CurriculumDeleteEntity::getId));
            return JsonSerialization.toJson(new BaseResponse<Page<CurriculumDeleteEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, curriculumDeleteEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String incrementCurriculum(IncrementCurriculumParam incrementCurriculumParam) throws IncrementCurriculumException {
        try {
            CurriculumEntity curriculumEntity = curriculumEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumEntity>()
                    .eq(CurriculumEntity::getCurriculumId, incrementCurriculumParam.getCurriculumId()));
            if (!(curriculumEntity == null)) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程序号已存在"
                ));
            }
            CurriculumEntity curriculumEntityNew = new CurriculumEntity();
            curriculumEntityNew.setCurriculumId(incrementCurriculumParam.getCurriculumId());
            curriculumEntityNew.setName(incrementCurriculumParam.getName());
            curriculumEntityNew.setCreditHour(incrementCurriculumParam.getCreditHour());
            curriculumEntityNew.setCapacity(incrementCurriculumParam.getCapacity());
            curriculumEntityNew.setIsExistence(true);
            curriculumEntityNew.setId(UUID.randomUUID().toString() + System.currentTimeMillis());
            curriculumEntityNew.setLocation(incrementCurriculumParam.getLocation());
            curriculumEntityNew.setCredits(incrementCurriculumParam.getCredits());
            curriculumEntityRepository.insert(curriculumEntityNew);
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new IncrementCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateCurriculum(UpdateCurriculumParam updateCurriculumParam) throws UpdateCurriculumException {
        try {
            if (curriculumEntityRepository.exists(new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getCurriculumId, updateCurriculumParam.getCurriculumId()
            ).ne(CurriculumEntity::getId, updateCurriculumParam.getId()))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程序号已存在"
                ));
            }
            CurriculumEntity curriculumEntity = curriculumEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getId, updateCurriculumParam.getId()
            ));
            curriculumEntity.setName(updateCurriculumParam.getName());
            curriculumEntity.setCreditHour(updateCurriculumParam.getCreditHour());
            curriculumEntity.setCapacity(updateCurriculumParam.getCapacity());
            curriculumEntity.setCredits(updateCurriculumParam.getCredits());
            curriculumEntity.setLocation(updateCurriculumParam.getLocation());
            curriculumEntity.setCurriculumId(updateCurriculumParam.getCurriculumId());
            curriculumEntityRepository.update(curriculumEntity, new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getId, updateCurriculumParam.getId()
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功"
            ));
        } catch (Exception exception) {
            throw new UpdateCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateDeleteCurriculum(UpdateCurriculumParam updateCurriculumParam) throws UpdateCurriculumException {
        try {
            if (curriculumDeleteEntityRepository.exists(new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                    CurriculumDeleteEntity::getCurriculumId, updateCurriculumParam.getCurriculumId()
            ).ne(CurriculumDeleteEntity::getId, updateCurriculumParam.getId()))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程序号已存在"
                ));
            }
            CurriculumDeleteEntity curriculumDeleteEntity = curriculumDeleteEntityRepository.selectOne(
                    new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                            CurriculumDeleteEntity::getId, updateCurriculumParam.getId()
                    ));
            curriculumDeleteEntity.setName(updateCurriculumParam.getName());
            curriculumDeleteEntity.setCreditHour(updateCurriculumParam.getCreditHour());
            curriculumDeleteEntity.setCapacity(updateCurriculumParam.getCapacity());
            curriculumDeleteEntity.setCredits(updateCurriculumParam.getCredits());
            curriculumDeleteEntity.setLocation(updateCurriculumParam.getLocation());
            curriculumDeleteEntity.setCurriculumId(updateCurriculumParam.getCurriculumId());
            curriculumDeleteEntityRepository.update(curriculumDeleteEntity, new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                    CurriculumDeleteEntity::getId, updateCurriculumParam.getId()
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功"
            ));
        } catch (Exception exception) {
            throw new UpdateCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String removeCurriculum(String curriculumId) throws MoveCurriculumException {
        try {
            CurriculumEntity curriculumEntity = curriculumEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getId, curriculumId
            ));
            if (curriculumEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程不存在"
                ));
            }
            if (curriculumDeleteEntityRepository.exists(new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                    CurriculumDeleteEntity::getCurriculumId, curriculumEntity.getCurriculumId()
            ))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程序号已在课程回收站中存在"
                ));
            }
            CurriculumDeleteEntity curriculumDeleteEntity = new CurriculumDeleteEntity();
            BeanUtils.copyProperties(curriculumEntity, curriculumDeleteEntity);
            curriculumDeleteEntityRepository.insert(curriculumDeleteEntity);
            curriculumEntityRepository.delete(new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getId, curriculumId
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "删除成功"
            ));
        } catch (Exception exception) {
            throw new MoveCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String moveCurriculum(String curriculumId) throws MoveCurriculumException {
        try {
            CurriculumDeleteEntity curriculumDeleteEntity = curriculumDeleteEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                    CurriculumDeleteEntity::getId, curriculumId
            ));
            if (curriculumDeleteEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程不存在"
                ));
            }
            if (curriculumEntityRepository.exists(new LambdaQueryWrapper<CurriculumEntity>().eq(
                    CurriculumEntity::getCurriculumId, curriculumDeleteEntity.getCurriculumId()
            ))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误课程序号已存在"
                ));
            }
            CurriculumEntity curriculumEntity = new CurriculumEntity();
            BeanUtils.copyProperties(curriculumDeleteEntity, curriculumEntity);
            curriculumEntityRepository.insert(curriculumEntity);
            curriculumDeleteEntityRepository.delete(new LambdaQueryWrapper<CurriculumDeleteEntity>().eq(
                    CurriculumDeleteEntity::getId, curriculumId
            ));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "移回成功"
            ));
        } catch (Exception exception) {
            throw new MoveCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String changIsExistence(String curriculumId) throws UpdateCurriculumException {
        try {
            CurriculumEntity curriculumEntity = curriculumEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumEntity>()
                    .eq(CurriculumEntity::getId, curriculumId));
            if (curriculumEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            curriculumEntity.setIsExistence(!curriculumEntity.getIsExistence());
            curriculumEntityRepository.update(curriculumEntity, new LambdaUpdateWrapper<CurriculumEntity>()
                    .eq(CurriculumEntity::getId, curriculumId));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功"
            ));
        } catch (Exception exception) {
            throw new UpdateCurriculumException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String changDeleteIsExistence(String curriculumId) throws UpdateCurriculumException {
        try {
            CurriculumDeleteEntity curriculumDeleteEntity = curriculumDeleteEntityRepository.selectOne(new LambdaQueryWrapper<CurriculumDeleteEntity>()
                    .eq(CurriculumDeleteEntity::getId, curriculumId));
            if (curriculumDeleteEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            curriculumDeleteEntity.setIsExistence(!curriculumDeleteEntity.getIsExistence());
            curriculumDeleteEntityRepository.update(curriculumDeleteEntity, new LambdaUpdateWrapper<CurriculumDeleteEntity>()
                    .eq(CurriculumDeleteEntity::getId, curriculumId));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功"
            ));
        } catch (Exception exception) {
            throw new UpdateCurriculumException(exception.getMessage());
        }
    }
}
