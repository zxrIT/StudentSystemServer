package com.user.management.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.user.management.service.entity.StudentEntity;
import org.springframework.stereotype.Repository;

@Repository
@DS("master_student")
public interface StudentRepository extends BaseMapper<StudentEntity> {
    Page<StudentEntity> selectStudentPage(Page<StudentEntity> page);

    Page<StudentEntity> selectStudentsByClass(Page<StudentEntity> page,
                                              @Param("studentClass") String studentClass);

    Page<StudentEntity> selectStudentsByGrade(Page<StudentEntity> page,
                                              @Param("studentGrade") String studentGrade);

    Page<StudentEntity> selectStudentsBasicInfoByClass(Page<StudentEntity> page,
                                                       @Param("studentClass") String studentClass);

    Page<StudentEntity> selectStudentsByName(Page<StudentEntity> page, @Param("studentName") String studentName);

    Page<StudentEntity> selectStudentById(Page<StudentEntity> page, @Param("studentId") String studentId);
}
