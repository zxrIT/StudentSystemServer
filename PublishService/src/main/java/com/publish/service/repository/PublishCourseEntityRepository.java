package com.publish.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.publish.service.entity.PublishCourseEntity;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_course_publish")
public interface PublishCourseEntityRepository extends BaseMapper<PublishCourseEntity> {
}
