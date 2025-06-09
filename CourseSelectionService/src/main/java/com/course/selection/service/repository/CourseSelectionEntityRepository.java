package com.course.selection.service.repository;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.selection.service.entity.CourseSelectionEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@DS("master_course_select")
public interface CourseSelectionEntityRepository extends BaseMapper<CourseSelectionEntity> {
    List<CourseSelectionEntity> selectStudentCourses(
            @Param("studentGrade") Integer studentGrade,
            @Param("collegeId") String collegeId,
            @Param("offset") long offset,
            @Param("pageSize") Integer pageSize
    );

    int countStudentCourses(
            @Param("studentGrade") Integer studentGrade,
            @Param("collegeId") String collegeId
    );
}
