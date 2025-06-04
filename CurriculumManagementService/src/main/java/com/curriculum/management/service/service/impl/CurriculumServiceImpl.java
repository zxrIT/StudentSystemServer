package com.curriculum.management.service.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.common.exception.entity.curriculum.SelectCurriculumException;
import com.common.response.entity.BaseResponse;
import com.common.response.entity.BaseResponseUtil;
import com.common.utils.JsonSerialization;
import com.curriculum.management.service.entity.CurriculumEntity;
import com.curriculum.management.service.repository.CurriculumEntityRepository;
import com.curriculum.management.service.service.CurriculumService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@SuppressWarnings("all")
@RequiredArgsConstructor
public class CurriculumServiceImpl extends ServiceImpl<CurriculumEntityRepository, CurriculumEntity>
        implements CurriculumService {
    @Autowired
    private final CurriculumEntityRepository curriculumEntityRepository;

    @Override
    public String getCurriculum(Integer quantity, Integer pages) {
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
}
