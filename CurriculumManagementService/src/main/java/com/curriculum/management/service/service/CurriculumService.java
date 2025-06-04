package com.curriculum.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.curriculum.management.service.entity.CurriculumEntity;

public interface CurriculumService extends IService<CurriculumEntity> {
    String getCurriculum(Integer quantity, Integer page);

    String getSelectCurriculum(String content, Integer quantity, Integer page);
}
