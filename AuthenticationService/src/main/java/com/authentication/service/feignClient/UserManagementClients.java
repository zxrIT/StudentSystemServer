package com.authentication.service.feignClient;

import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-management-service")
public interface UserManagementClients {

    @GetMapping("/api/user/getStudentByStudentId/{studentId}")
    Student getStudentByStudentId(@PathVariable String studentId);

    @GetMapping("/api/user/getTeacherByTeacherId/{TeacherId}")
    Teacher getTeacherByTeacherId(@PathVariable String TeacherId);

    @GetMapping("/api/user/getAdminByAdminId/{AdminId}")
    Student getAdminByAdminId(@PathVariable String AdminId);
}
