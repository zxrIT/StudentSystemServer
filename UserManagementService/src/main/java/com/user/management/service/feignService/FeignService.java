package com.user.management.service.feignService;

import com.user.data.entity.Student;
import com.user.data.entity.Teacher;

public interface FeignService {
    Student getStudentByStudentId(String studentId);

    Teacher getTeacherByTeacherId(String teacherId);

    Student getAdminByAdminId(String adminId);
}
