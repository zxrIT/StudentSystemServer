package com.course.selection.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.course.selection.service.entity.CourseSelectionEntity;

public interface CourseSelection extends IService<CourseSelectionEntity> {
    String getStudentCourse(Integer quantity, Integer page);
}
