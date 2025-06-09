package com.curriculum.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.curriculum.management.service.entity.CurriculumEntity;
import com.curriculum.management.service.request.IncrementCurriculumParam;
import com.curriculum.management.service.request.UpdateCurriculumParam;

public interface CurriculumService extends IService<CurriculumEntity> {
    String getCurriculum(Integer quantity, Integer page);

    String getCurriculumById(String courseId);

    String getCurriculumDelete(Integer quantity, Integer page);

    String getSelectCurriculum(String content, Integer quantity, Integer page);

    String getDeleteSelectCurriculum(String content, Integer quantity, Integer page);

    String incrementCurriculum(IncrementCurriculumParam incrementCurriculumParam);

    String updateCurriculum(UpdateCurriculumParam updateCurriculumParam);

    String updateDeleteCurriculum(UpdateCurriculumParam updateCurriculumParam);

    String removeCurriculum(String curriculumId);

    String moveCurriculum(String curriculumId);

    String changIsExistence(String curriculumId);

    String changDeleteIsExistence(String curriculumId);
}
