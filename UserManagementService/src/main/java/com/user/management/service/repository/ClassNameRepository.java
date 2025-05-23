package com.user.management.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.user.management.service.entity.ClassNameEntity;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_class")
public interface ClassNameRepository extends BaseMapper<ClassNameEntity> {
}
