package com.course.selection.service.feignClients;

import com.user.data.entity.College;
import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("user-management-service")
public interface UserManagementClients {

    @GetMapping("/api/user/getStudentByStudentId/{studentId}")
    Student getStudentByStudentId(@PathVariable String studentId);

    @GetMapping("/api/user/getTeacherByTeacherId/{TeacherId}")
    Teacher getTeacherByTeacherId(@PathVariable String TeacherId);

    @GetMapping("/api/user/getCollegeByName/{collegeName}")
    College getCollegeByCollegeName(@PathVariable String collegeName);

    @PostMapping("/api/user/getCourseBatch")
    Map<String, Map<String, String>> getCourseBatch(@RequestBody Map<String, Map<String, String>> requestBody);
}
