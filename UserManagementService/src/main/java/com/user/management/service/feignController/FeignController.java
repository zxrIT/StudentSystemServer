package com.user.management.service.feignController;

import com.user.data.entity.College;
import com.user.data.entity.Student;
import com.user.data.entity.Teacher;
import com.user.management.service.feignService.FeignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "远程调用接口管理", description = "远程调用接口相关操作API")
public class FeignController {
    @Autowired
    private final FeignService feignService;

    @Operation(summary = "根据学生学号查询学生数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getStudentByStudentId/{studentId}")
    public Student getStudentById(@PathVariable String studentId) {
        return feignService.getStudentByStudentId(studentId);
    }

    @Operation(summary = "根据教师工号查询教师数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getTeacherByTeacherId/{teacherId}")
    public Teacher getTeacherById(@PathVariable String teacherId) {
        return feignService.getTeacherByTeacherId(teacherId);
    }

    @Operation(summary = "根据教师工号和学院代码查询教师数据和学院数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/getCourseBatch")
    public Map<String, Map<String, String>> getCourseBatch(@RequestBody Map<String, Map<String, String>> requestBody) {
        return feignService.getCourseBatch(requestBody);
    }

    @Operation(summary = "根据管理员工号查询管理员数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getAdminByAdminId/{adminId}")
    public Student getAdminById(@PathVariable String adminId) {
        return feignService.getAdminByAdminId(adminId);
    }

    @Operation(summary = "根据学院名字获取学院数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getCollegeByName/{collegeName}")
    public College getCollegeByname(@PathVariable String collegeName) {
        return feignService.getCollegeByCollegeId(collegeName);
    }
}
