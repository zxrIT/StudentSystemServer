package com.authentication.service.repository;

import com.authentication.service.entity.TeacherAuth;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_teacher")
public interface TeacherAuthRepository extends BaseMapper<TeacherAuth> {
}
