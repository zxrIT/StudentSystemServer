package com.user.management.service.service.impl;

import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.user.IncrementCollegeException;
import com.common.exception.entity.user.SelectCollegeException;
import com.common.exception.entity.user.UpdateCollegeException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.user.management.service.entity.CollegeEntity;
import com.user.management.service.repository.CollegeRepository;
import com.user.management.service.request.IncrementCollegeParam;
import com.user.management.service.request.UpdateCollegeIsExistenceParam;
import com.user.management.service.request.UpdateCollegeParam;
import com.user.management.service.service.CollegeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("all")
public class CollegeServiceImpl extends ServiceImpl<CollegeRepository, CollegeEntity>
        implements CollegeService {
    private final CollegeRepository collegeRepository;

    @Override
    public String getCollege(Integer quantity, Integer pages) throws SelectCollegeException {
        try {
            if (quantity <= 0 || pages <= 0) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误"
                ));
            }
            Page<CollegeEntity> page = new Page<>(pages, quantity);
            Page<CollegeEntity> collegeEntityPage = collegeRepository.selectCollegePage(page);
            return JsonSerialization.toJson(new BaseResponse<Page<CollegeEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, collegeEntityPage
            ));
        } catch (Exception exception) {
            throw new SelectCollegeException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String incrementCollege(IncrementCollegeParam incrementCollegeParam) throws IncrementCollegeException {
        try {
            CollegeEntity collegeEntity = collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(
                    CollegeEntity::getCollegeId, incrementCollegeParam.getCollegeId()
            ));
            if (!(collegeEntity == null)) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误,学院编号已存在"
                ));
            }
            CollegeEntity newCollegeEntity = new CollegeEntity();
            newCollegeEntity.setCollegeId(incrementCollegeParam.getCollegeId());
            newCollegeEntity.setCollegeName(incrementCollegeParam.getCollegeName());
            newCollegeEntity.setIsExistence(true);
            newCollegeEntity.setClassCount(incrementCollegeParam.getClassCount());
            newCollegeEntity.setId(UUID.randomUUID().toString() + System.currentTimeMillis());
            collegeRepository.insert(newCollegeEntity);
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "添加成功"
            ));
        } catch (Exception exception) {
            throw new IncrementCollegeException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateCollege(UpdateCollegeParam updateCollegeParam) throws UpdateCollegeException {
        try {
            CollegeEntity collegeEntity =
                    collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>()
                            .eq(CollegeEntity::getId, updateCollegeParam.getId()));
            if (collegeEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，学院不存在"
                ));
            }
            if (collegeRepository.exists(new LambdaQueryWrapper<CollegeEntity>().eq(CollegeEntity::getCollegeId,
                    updateCollegeParam.getCollegeId()).ne(CollegeEntity::getId, updateCollegeParam.getId()))) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，学院代码已存在"
                ));
            }
            collegeEntity.setCollegeId(updateCollegeParam.getCollegeId());
            collegeEntity.setCollegeName(updateCollegeParam.getCollegeName());
            collegeRepository.update(collegeEntity, new LambdaUpdateWrapper<CollegeEntity>()
                    .eq(CollegeEntity::getId, updateCollegeParam.getId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, "修改成功有部分数据未变下次登录后刷新"
            ));
        } catch (Exception exception) {
            throw new UpdateCollegeException(exception.getMessage());
        }
    }

    @Override
    public String getCollegeNames() throws SelectCollegeException {
        try {
            List<CollegeEntity> collegeEntities = collegeRepository.selectCollegeNames();
            return JsonSerialization.toJson(new BaseResponse<List<CollegeEntity>>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE, collegeEntities
            ));
        } catch (Exception exception) {
            throw new SelectCollegeException(exception.getMessage());
        }
    }

    @Override
    @DSTransactional(rollbackFor = Exception.class)
    public String updateCollegeIsExistence(UpdateCollegeIsExistenceParam updateCollegeIsExistenceParam) throws UpdateCollegeException {
        try {
            CollegeEntity collegeEntity =
                    collegeRepository.selectOne(new LambdaQueryWrapper<CollegeEntity>().eq(CollegeEntity::getId,
                            updateCollegeIsExistenceParam.getId()));
            if (collegeEntity == null) {
                log.error("请求参数错误");
                return JsonSerialization.toJson(new BaseResponse<String>(
                        BaseResponseUtil.CLIENT_ERROR_CODE, BaseResponseUtil.CLIENT_ERROR_MESSAGE, "请求参数错误，学院不存在"
                ));
            }
            collegeEntity.setIsExistence(!collegeEntity.getIsExistence());
            collegeRepository.update(collegeEntity, new LambdaUpdateWrapper<CollegeEntity>().
                    eq(CollegeEntity::getId, updateCollegeIsExistenceParam.getId()));
            return JsonSerialization.toJson(new BaseResponse<String>(
                    BaseResponseUtil.SUCCESS_CODE, BaseResponseUtil.SUCCESS_MESSAGE,
                    collegeEntity.getIsExistence().equals(true) ? "启用成功" : "停用成功"
            ));
        } catch (Exception exception) {
            throw new UpdateCollegeException(exception.getMessage());
        }
    }
}
