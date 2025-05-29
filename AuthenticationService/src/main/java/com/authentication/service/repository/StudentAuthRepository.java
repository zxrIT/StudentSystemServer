package com.authentication.service.repository;

import com.authentication.service.entity.StudentAuth;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_student")
public interface StudentAuthRepository extends BaseMapper<StudentAuth> {
}
