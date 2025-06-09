package com.user.management.service.feignService;

import com.user.data.entity.College;
import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

public interface FeignService {
    Student getStudentByStudentId(String studentId);

    Teacher getTeacherByTeacherId(String teacherId);

    College getCollegeByCollegeId(String collegeId);

    Student getAdminByAdminId(String adminId);

    Map<String, Map<String, String>> getCourseBatch(@RequestBody Map<String, Map<String, String>> requestBody);
}
