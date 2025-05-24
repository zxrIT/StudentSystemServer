package com.user.management.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.user.management.service.entity.TeacherEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@DS("master_teacher")
public interface TeacherEntityRepository extends BaseMapper<TeacherEntity> {
    Page<TeacherEntity> selectTeacherPage(Page<TeacherEntity> page);

    Page<TeacherEntity> selectTeacherByCollege(Page<TeacherEntity> page,
                                               @Param("teacherCollege") String teacherCollege);

    Page<TeacherEntity> selectTeacherById(Page<TeacherEntity> page,
                                          @Param("teacherId") String teacherId);

    Page<TeacherEntity> selectTeacherByName(Page<TeacherEntity> page,
                                            @Param("teacherName") String teacherName);

    List<TeacherEntity> selectAllCounselor();

    List<TeacherEntity> selectAllTeacher();
}
