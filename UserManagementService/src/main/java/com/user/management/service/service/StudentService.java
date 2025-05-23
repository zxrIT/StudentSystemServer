package com.user.management.service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.user.management.service.entity.StudentEntity;
import com.user.management.service.request.ResetPasswordParam;
import com.user.management.service.request.UpdateStudentParam;

public interface StudentService extends IService<StudentEntity> {
    String getStudent(Integer quantity, Integer pages);

    String getSelectStudent(Integer payload, String content, Integer quantity, Integer pages);

    String resetPassword(ResetPasswordParam resetPasswordParam);

    String updateStudent(UpdateStudentParam updateStudentParam);

    String deleteStudent(String studentId);

    String getStudentByClass(Integer quantity, Integer pages, String className);
}
