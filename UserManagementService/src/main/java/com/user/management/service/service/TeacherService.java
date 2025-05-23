package com.user.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.management.service.entity.TeacherEntity;
import com.user.management.service.request.UpdateTeacherParam;

public interface TeacherService extends IService<TeacherEntity> {
    String getTeacher(Integer quantity, Integer page);

    String updateTeacher(UpdateTeacherParam updateTeacherParam);

    String resetPassword(String teacherId);

    String getSelectTeacher(Integer payload, String content, Integer quantity, Integer pages);
}
