package com.curriculum.management.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.curriculum.management.service.entity.CurriculumDeleteEntity;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_curriculum")
public interface CurriculumDeleteEntityRepository extends BaseMapper<CurriculumDeleteEntity> {
}
