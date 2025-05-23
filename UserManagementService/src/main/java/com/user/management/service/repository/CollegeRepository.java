package com.user.management.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.management.service.entity.CollegeEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@DS("master_college")
public interface CollegeRepository extends BaseMapper<CollegeEntity> {
    Page<CollegeEntity> selectCollegePage(Page<CollegeEntity> page);
    List<CollegeEntity> selectCollegeNames();
}
