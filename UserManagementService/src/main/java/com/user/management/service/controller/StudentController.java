package com.user.management.service.controller;

import com.user.management.service.request.ResetPasswordParam;
import com.user.management.service.request.UpdateStudentParam;
import com.user.management.service.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@RequestMapping("/student")
@SuppressWarnings("all")
@RequiredArgsConstructor
@Tag(name = "学生管理", description = "学生相关操作API")
public class StudentController {
    @Autowired
    private final StudentService studentService;

    @Operation(summary = "查询学生数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getStudent/{quantity}/{page}")
    public String getStudent(@PathVariable Integer quantity, @PathVariable Integer page) {
        return studentService.getStudent(quantity, page);
    }

    @Operation(summary = "查询班级学生数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getStudentByClass/{quantity}/{page}/{className}")
    public String getStudentByClass(@PathVariable Integer quantity, @PathVariable Integer page, @PathVariable String className) {
        return studentService.getStudentByClass(quantity, page, className);
    }

    @Operation(summary = "查询搜索学生数据")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @GetMapping("/getSelectStudent/{payload}/{content}/{quantity}/{page}")
    public String getSelectStudent(@PathVariable Integer payload, @PathVariable String content,
                                   @PathVariable Integer quantity, @PathVariable Integer page) {
        return studentService.getSelectStudent(payload, content, quantity, page);
    }

    @Operation(summary = "重置学生密码")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "重置成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，学生信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody ResetPasswordParam resetPasswordParam) {
        return studentService.resetPassword(resetPasswordParam);
    }

    @Operation(summary = "修改学生信息")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "修改成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，学生信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @PutMapping("/updateStudent")
    public String updateStudent(@RequestBody UpdateStudentParam updateStudentParam) {
        return studentService.updateStudent(updateStudentParam);
    }

    @Operation(summary = "删除学生")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "300", description = "请求参数错误，学生信息不存在"),
            @ApiResponse(responseCode = "500", description = "服务端错误")
    })
    @DeleteMapping("/deleteStudent/{studentId}")
    public String deleteStudent(@PathVariable String studentId) {
        return studentService.deleteStudent(studentId);
    }
}
